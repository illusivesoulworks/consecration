/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.events;

import c4.consecration.Consecration;
import c4.consecration.common.init.ConsecrationBlocks;
import c4.consecration.common.init.ConsecrationItems;
import c4.consecration.common.init.ConsecrationPotions;
import c4.consecration.common.util.UndeadHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.Level;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class EventHandlerCommon {

    private static Random rand = new Random();
    private static final Method START_CONVERTING = ReflectionHelper.findMethod(EntityZombieVillager.class, "startConverting", "func_191991_a", UUID.class, Integer.TYPE);

    @SubscribeEvent
    @SuppressWarnings("ConstantConditions")
    public void onSpawnCheck(LivingSpawnEvent.CheckSpawn evt) {

        if (UndeadHelper.isUndead(evt.getEntityLiving())) {
            World world = evt.getWorld();
            BlockPos pos = evt.getEntityLiving().getPosition();

            if (world.getBlockState(pos).getBlock() == ConsecrationBlocks.blessedTrail
                    || world.getBlockState(pos.down(2)).getBlock() == ConsecrationBlocks.blessedTrail)
                evt.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("ConstantConditions")
    public void onThrowable(ProjectileImpactEvent.Throwable evt) {

        if (!evt.getThrowable().world.isRemote) {

            EntityThrowable entityThrowable = evt.getThrowable();

            if (entityThrowable instanceof EntityPotion) {
                ItemStack stack = ((EntityPotion) entityThrowable).getPotion();
                List<PotionEffect> list = PotionUtils.getEffectsFromStack(stack);
                boolean holyPotion = false;
                if (list.isEmpty()) {
                    return;
                } else {
                    for (PotionEffect effect : list) {
                        if (effect.getPotion() == ConsecrationPotions.HOLY_POTION) {
                            holyPotion = true;
                            break;
                        }
                    }
                }
                if (!holyPotion) {
                    return;
                }

                AxisAlignedBB axisalignedbb = entityThrowable.getEntityBoundingBox().grow(4.0D, 2.0D, 4.0D);
                List<EntityItem> items = entityThrowable.world.getEntitiesWithinAABB(EntityItem.class, axisalignedbb,
                        apply -> apply != null && apply.getItem().getItem() == Items.GLOWSTONE_DUST);

                if (items.isEmpty()) {
                    return;
                }

                for (EntityItem item : items) {
                    EntityItem newItem = new EntityItem(item.world, item.posX, item.posY, item.posZ, new ItemStack
                            (ConsecrationItems.blessedDust, item.getItem().getCount()));
                    item.world.spawnEntity(newItem);
                    item.setDead();
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingDeath(LivingDeathEvent evt) {

        EntityLivingBase entity = evt.getEntityLiving();

        if (!entity.getEntityWorld().isRemote && entity instanceof EntityZombieVillager && UndeadHelper.isHolyDamage(evt.getSource().getDamageType())) {
            EntityZombieVillager zombieVillager = (EntityZombieVillager) entity;
            if (!zombieVillager.isConverting()) {
                convertZombieVillager(zombieVillager, evt);
            }
        }
    }

    private void convertZombieVillager(EntityZombieVillager zombieVillager, LivingDeathEvent evt) {

        if (zombieVillager.isConverting()) {
            return;
        }

        if (evt.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase entitylivingbase = (EntityLivingBase) evt.getSource().getTrueSource();
            try {
                START_CONVERTING.invoke(zombieVillager, entitylivingbase.getUniqueID(), rand.nextInt(2401) + 3600);
            } catch (IllegalAccessException | InvocationTargetException e) {
                Consecration.logger.log(Level.ERROR, "Error in startConverting for entity " + zombieVillager);
            }
            evt.setCanceled(true);
            zombieVillager.setHealth(1);
        }
    }
}
