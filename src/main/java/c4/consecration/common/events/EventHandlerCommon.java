/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.events;

import c4.consecration.Consecration;
import c4.consecration.common.capabilities.CapabilityUndying;
import c4.consecration.common.capabilities.IUndying;
import c4.consecration.common.config.ConfigHandler;
import c4.consecration.common.init.ConsecrationBlocks;
import c4.consecration.common.init.ConsecrationFluids;
import c4.consecration.common.init.ConsecrationItems;
import c4.consecration.common.init.ConsecrationPotions;
import c4.consecration.common.util.UndeadHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemRedstone;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;
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
        World world = evt.getWorld();
        BlockPos pos = evt.getEntityLiving().getPosition();

        if (world.getBlockState(pos).getBlock() == ConsecrationBlocks.blessedTrail
                || world.getBlockState(pos.down(2)).getBlock() == ConsecrationBlocks.blessedTrail) {

            if (UndeadHelper.isUndead(evt.getEntityLiving())) {
                evt.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("ConstantConditions")
    public void onPriestInteract(PlayerInteractEvent.EntityInteract evt) {
        Entity target = evt.getTarget();

        if (!target.world.isRemote && target instanceof EntityVillager) {
            EntityVillager villager = (EntityVillager)target;
            VillagerRegistry.VillagerProfession profession = villager.getProfessionForge();

            if (profession.getRegistryName().toString().equalsIgnoreCase("minecraft:priest")) {
                ItemStack stack = evt.getItemStack();
                Item item = stack.getItem();
                EntityPlayer player = evt.getEntityPlayer();
                ItemStack output = ItemStack.EMPTY;
                String messageKey = "consecration.benediction.";

                if (item instanceof ItemBucket) {
                    FluidStack fluidStack = FluidUtil.getFluidContained(stack);

                    if (fluidStack != null && fluidStack.getFluid() == FluidRegistry.WATER) {
                        output = FluidUtil.getFilledBucket(new FluidStack(ConsecrationFluids.HOLY_WATER,
                                Fluid.BUCKET_VOLUME));
                        messageKey += "water";
                    }
                } else if (item == Items.GLOWSTONE_DUST) {
                    output = new ItemStack(ConsecrationItems.blessedDust, stack.getCount());
                    messageKey += "dust";
                }

                if (!output.isEmpty()) {
                    if (player.experienceLevel < 1 && !player.capabilities.isCreativeMode) {
                        messageKey = "consecration.benediction.power";
                        player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_VILLAGER_NO,
                                SoundCategory.NEUTRAL, 1.0F,
                                (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.2F + 1.0F);
                    } else {
                        player.onEnchant(output, 1);
                        stack.shrink(output.getCount());
                        ItemHandlerHelper.giveItemToPlayer(player, output, player.inventory.currentItem);
                        player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                                SoundCategory.BLOCKS, 2.0F, player.world.rand.nextFloat() * 0.5f + 1.5f);
                        player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_VILLAGER_YES,
                                SoundCategory.NEUTRAL, 1.0F,
                                (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.2F + 1.0F);
                    }
                    player.sendStatusMessage(new TextComponentTranslation(messageKey), true);
                    evt.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent evt) {
        EntityLivingBase entityLivingBase = evt.getEntityLiving();
        DamageSource source = evt.getSource();

        if (!entityLivingBase.getEntityWorld().isRemote && source.getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase)source.getTrueSource();
            IUndying undying = CapabilityUndying.getUndying(attacker);

            if (undying != null) {
                int level = 0;

                for (ItemStack stack : entityLivingBase.getArmorInventoryList()) {
                    if (!stack.isEmpty() && UndeadHelper.isHolyArmor(stack)) {
                        level++;
                    }
                }

                if (level > 0 && entityLivingBase.getEntityWorld().rand.nextFloat() < 0.15F * (float)level ) {
                    undying.setSmite(ConfigHandler.holy.smiteDuration * 20);
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
