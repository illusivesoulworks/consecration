/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common;

import c4.consecration.Consecration;
import c4.consecration.init.ModPotions;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.Level;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.TinkerTraits;
import xreliquary.entities.EntityGlowingWater;
import xreliquary.entities.EntityHolyHandGrenade;
import xreliquary.items.ItemMercyCross;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class EventHandlerCommon {

    private static Random rand = new Random();
    private static final Method START_CONVERTING = ReflectionHelper.findMethod(EntityZombieVillager.class, "startConverting", "func_191991_a", UUID.class, Integer.TYPE);

    @SubscribeEvent
    public void onPlayerAttack(AttackEntityEvent evt) {

        //Don't load this feature if survivalist is also loaded
        if (!Loader.isModLoaded("survivalist")) {
            Entity entity = evt.getTarget();

            if (entity.world.isRemote) {
                return;
            }

            if (!entity.isImmuneToFire() && entity instanceof EntityLivingBase) {
                EntityPlayer player = evt.getEntityPlayer();
                ItemStack stack = player.getHeldItemMainhand();
                if (stack.getItem() instanceof ItemBlock) {
                    ItemBlock itemblock = (ItemBlock) stack.getItem();
                    if (itemblock.getBlock() == Blocks.TORCH) {
                        entity.setFire(2);
                        if (rand.nextFloat() < 0.25F && !player.isCreative()) {
                            stack.shrink(1);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onThrowable(ProjectileImpactEvent.Throwable evt) {

        if (Loader.isModLoaded("xreliquary") && !evt.getThrowable().world.isRemote) {

            EntityThrowable entityThrowable = evt.getThrowable();

            if (entityThrowable instanceof EntityGlowingWater) {

                AxisAlignedBB bb = entityThrowable.getEntityBoundingBox().grow(4.0D, 2.0D, 4.0D);
                List<EntityLivingBase> eList = entityThrowable.world.getEntitiesWithinAABB(EntityLivingBase.class, bb);
                for (EntityLivingBase entity : eList) {
                    if (entity.isEntityUndead()) {
                        entity.addPotionEffect(new PotionEffect(ModPotions.SMITE_POTION, 2, 0, false, false));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onUndeath(LivingDeathEvent evt) {

        if (!evt.getEntityLiving().getEntityWorld().isRemote) {

            if (evt.getEntityLiving().isEntityUndead()) {

                EntityLivingBase entity = evt.getEntityLiving();

                if (entity.isPotionActive(ModPotions.SMITE_POTION)) {
                    return;
                }

                if (!evt.getSource().damageType.equals(ModPotions.HOLY_DAMAGE.damageType) && !isNaturalDamage(evt.getSource())) {

                    Entity immediateSource = evt.getSource().getImmediateSource();

                    //Fire damage will still kill undead if they're not immune to fire
                    if (!entity.isImmuneToFire() && evt.getSource().isFireDamage()) {
                        return;
                    }

                    //Iron Golems are still capable of defending villages from the undead
                    if (immediateSource instanceof EntityIronGolem) {
                        return;
                    }

                    boolean smite = false;

                    //Smite will still work also
                    if (immediateSource instanceof EntityLivingBase) {
                        smite = isSmiteWeapon(((EntityLivingBase) immediateSource).getHeldItemMainhand(), evt.getEntityLiving());
                    } else if (Loader.isModLoaded("xreliquary") ) {
                        if (immediateSource instanceof EntityHolyHandGrenade) {
                            smite = true;
                        }
                    }

                    if (smite) {
                        return;
                    }

                    cancelUndeath(evt);

                } else if (entity instanceof EntityZombieVillager) {

                    EntityZombieVillager zombievillager = (EntityZombieVillager) entity;

                    if (zombievillager.isConverting()) {
                        cancelUndeath(evt);
                        return;
                    }

                    if (evt.getSource().getTrueSource() instanceof EntityLivingBase) {
                        EntityLivingBase entitylivingbase = (EntityLivingBase) evt.getSource().getTrueSource();
                        try {
                            START_CONVERTING.invoke(zombievillager, entitylivingbase.getUniqueID(), rand.nextInt(2401) + 3600);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            Consecration.logger.log(Level.ERROR, "Error in startConverting for entity " + zombievillager);
                        }
                        evt.setCanceled(true);
                        entity.setHealth(1);
                    }
                }
            }
        }
    }

    private static void cancelUndeath(LivingDeathEvent evt) {

        EntityLivingBase entity = evt.getEntityLiving();
        evt.setCanceled(true);
        entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 1200, 2));
        entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 1200, 2));
        entity.setHealth(1);
    }

    private static boolean isSmiteWeapon(ItemStack stack, EntityLivingBase target) {

        //Smite enchantment
        if (EnchantmentHelper.getModifierForCreature(stack, target.getCreatureAttribute()) > 0) {
            return true;
        }

        //Silver tools/weapons
        if (stack.getItem() instanceof ItemTool && isSilverTool((ItemTool) stack.getItem())) {
            return true;
        }

        //Silver TiCon tools/weapons
        if (Loader.isModLoaded("tconstruct") && stack.getItem() instanceof TinkersItem) {
            if (TConstruct.pulseManager.isPulseLoaded(TinkerTools.PulseId)) {
                if (TinkerUtil.hasTrait(TagUtil.getTagSafe(stack), TinkerTraits.holy.getIdentifier()) || TinkerUtil.hasModifier(TagUtil.getTagSafe(stack), TinkerModifiers.modSmite.getIdentifier())) {
                    return true;
                }
            }
        }

        //Cross of Mercy from Reliquary
        if (Loader.isModLoaded("xreliquary")) {
            if (stack.getItem() instanceof ItemMercyCross) {
                return true;
            }
        }

        return false;
    }

    private static boolean isNaturalDamage(DamageSource source) {
        return source == DamageSource.IN_WALL || source == DamageSource.CRAMMING || source == DamageSource.OUT_OF_WORLD;
    }

    private static boolean isSilverTool(ItemTool tool) {

        String materialName = tool.getToolMaterialName();

        if (materialName != null) {
            if (materialName.equalsIgnoreCase("SILVER")) {
                return true;
            }
            int colonIndex = materialName.lastIndexOf(":");
            if (colonIndex >= 0 && colonIndex < materialName.length()) {
                return materialName.substring(colonIndex).equalsIgnoreCase("SILVER");
            }
        }
        return false;
    }
}
