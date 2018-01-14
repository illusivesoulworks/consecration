/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common;

import blusunrize.immersiveengineering.api.Lib;
import c4.consecration.Consecration;
import c4.consecration.init.ModItems;
import c4.consecration.init.ModPotions;
import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Level;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.BlockSoil;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.TinkerTraits;
import tamaized.aov.registry.AoVDamageSource;
import xreliquary.entities.EntityGlowingWater;
import xreliquary.entities.EntityHolyHandGrenade;
import xreliquary.items.ItemMercyCross;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class EventHandlerCommon {

    private static Random rand = new Random();
    private static final Method START_CONVERTING = ReflectionHelper.findMethod(EntityZombieVillager.class, "startConverting", "func_191991_a", UUID.class, Integer.TYPE);

    @SubscribeEvent
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
                        if (effect.getPotion() == ModPotions.HOLY_POTION) {
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
                        (apply) -> apply.getItem().getItem() == Items.GLOWSTONE_DUST);

                if (items.isEmpty()) {
                    return;
                }

                for (EntityItem item : items) {
                    EntityItem newItem = new EntityItem(item.world, item.posX, item.posY, item.posZ, new ItemStack(ModItems.blessedDust, item.getItem().getCount()));
                    item.world.spawnEntity(newItem);
                    item.setDead();
                }
            } else if (Loader.isModLoaded("xreliquary") && entityThrowable instanceof EntityGlowingWater) {

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

    @SubscribeEvent (priority = EventPriority.HIGHEST)
    public void onUndeath(LivingDeathEvent evt) {

        if (!evt.getEntityLiving().getEntityWorld().isRemote) {

            if (evt.getEntityLiving().isEntityUndead()) {

                EntityLivingBase entity = evt.getEntityLiving();

                if (entity.isPotionActive(ModPotions.SMITE_POTION)) {
                    return;
                }

                //Consecrated Soil compatibility
                if (Loader.isModLoaded("tconstruct")) {
                    BlockPos standingOn = entity.getPosition().down();
                    IBlockState state = entity.world.getBlockState(standingOn);
                    if (state.getBlock() == TinkerCommons.blockSoil) {
                        if (state.getValue(BlockSoil.TYPE) == BlockSoil.SoilTypes.CONSECRATED) {
                            return;
                        }
                    }
                }

                if (!isHolyDamage(evt.getSource()) && !isNaturalDamage(evt.getSource())) {

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

    private static boolean isHolyDamage(DamageSource source) {
        String damageType = source.getDamageType();
        boolean flag = false;
        if (Loader.isModLoaded("immersiveengineering")) {
            flag = damageType.equals(Lib.DMG_RevolverSilver);
        }
        if (!flag && Loader.isModLoaded("aov")) {
            flag = source == AoVDamageSource.NIMBUS;
        }
        return damageType.equals(ModPotions.HOLY_DAMAGE.damageType) || flag;
    }

    private static boolean isSmiteWeapon(ItemStack stack, EntityLivingBase target) {

        //Smite enchantment
        if (EnchantmentHelper.getModifierForCreature(stack, target.getCreatureAttribute()) > 0) {
            return true;
        }

        //Silver tools/weapons
        if (stack.getItem() instanceof ItemTool && isSilverMaterial(((ItemTool) stack.getItem()).getToolMaterialName())) {
            return true;
        } else if (stack.getItem() instanceof ItemSword && isSilverMaterial(((ItemSword) stack.getItem()).getToolMaterialName())) {
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

    private static boolean isSilverMaterial(String materialName) {

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
