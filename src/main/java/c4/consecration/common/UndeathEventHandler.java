/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common;

import api.materials.PartMaterial;
import blusunrize.immersiveengineering.api.Lib;
import c4.consecration.Consecration;
import c4.consecration.config.ConfigHandler;
import c4.consecration.init.ModPotions;
import net.darkhax.gamestages.capabilities.PlayerDataHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.Level;
import defeatedcrow.hac.magic.proj.EntityProjSilver;
import defeatedcrow.hac.main.entity.EntitySilverBullet;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.BlockSoil;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.TinkerTraits;
import tamaized.aov.registry.AoVDamageSource;
import toolbox.common.items.tools.*;
import xreliquary.entities.EntityGlowingWater;
import xreliquary.entities.EntityHolyHandGrenade;
import xreliquary.items.ItemMercyCross;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class UndeathEventHandler {

    private static Random rand = new Random();
    private static final Method START_CONVERTING = ReflectionHelper.findMethod(EntityZombieVillager.class, "startConverting", "func_191991_a", UUID.class, Integer.TYPE);

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onUndeath(LivingDeathEvent evt) {

        if (!evt.getEntityLiving().getEntityWorld().isRemote) {

            if (evt.getEntityLiving().isEntityUndead() || isHonoraryUndead(evt.getEntityLiving())) {

                EntityLivingBase entity = evt.getEntityLiving();

                if (!isAllowedDimension(entity)) {
                    return;
                }

                if (evt.getSource().getTrueSource() instanceof EntityPlayer) {
                    if (Loader.isModLoaded("gamestages") && !hasRequiredStage((EntityPlayer) evt.getSource().getTrueSource())) {
                        return;
                    }
                }

                if (!isHolyDamage(entity, evt.getSource()) && !isNaturalDamage(evt.getSource())) {

                    Entity immediateSource = evt.getSource().getImmediateSource();

                    //Fire damage will still kill undead if they're not immune to fire
                    if (!entity.isImmuneToFire() && evt.getSource().isFireDamage()) {
                        return;
                    }

                    //Iron Golems are still capable of defending villages from the undead
                    if (immediateSource instanceof EntityIronGolem) {
                        return;
                    }

                    cancelUndeath(evt);
                }

                if (ModPotions.HOLY_DAMAGE.damageType.equals(evt.getSource().getDamageType()) && entity instanceof EntityZombieVillager) {
                    EntityZombieVillager zombieVillager = (EntityZombieVillager) entity;
                    if (!zombieVillager.isConverting()) {
                        convertZombieVillager(zombieVillager, evt);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onThrowable(ProjectileImpactEvent.Throwable evt) {

        if (!evt.getThrowable().world.isRemote) {

            EntityThrowable entityThrowable = evt.getThrowable();

            if (Loader.isModLoaded("xreliquary") && entityThrowable instanceof EntityGlowingWater) {

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

    private boolean isHolyDamage(EntityLivingBase target, DamageSource source) {

        String damageType = source.getDamageType();

        //Workaround to capture holy damage if there's no other way
        if (target.isPotionActive(ModPotions.SMITE_POTION)) {
            return true;
        } else if (damageType.equals(ModPotions.HOLY_DAMAGE.damageType)) {
            return true;
        } else {

            //Immersive Engineering Silver Bullets
            if (Loader.isModLoaded("immersiveengineering")) {
                if (damageType.equals(Lib.DMG_RevolverSilver)) {
                    return true;
                }
            }

            //Angel of Vengenace Nimbus Holy Spells
            if (Loader.isModLoaded("aov")) {
                if (source == AoVDamageSource.NIMBUS) {
                    return true;
                }
            }

            if (source.getImmediateSource() instanceof EntityLivingBase) {

                ItemStack stack = ((EntityLivingBase) source.getImmediateSource()).getHeldItemMainhand();

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

                //Silver Adventurer's Toolbox tools/weapons
                if (Loader.isModLoaded("toolbox")) {
                    if (isSilverToolbox(stack)) {
                        return true;
                    }
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
            } else {

                Entity immediateSource = source.getImmediateSource();

                //Tinkers' Construct Consecrated Soil
                if (Loader.isModLoaded("tconstruct")) {

                    //Consecrated Soil compatibility
                    BlockPos standingOn = target.getPosition().down();
                    IBlockState state = target.world.getBlockState(standingOn);
                    if (state.getBlock() == TinkerCommons.blockSoil) {
                        if (state.getValue(BlockSoil.TYPE) == BlockSoil.SoilTypes.CONSECRATED) {
                            return true;
                        }
                    }

                    //Silver TiCon arrows/bolts
                    if (immediateSource instanceof EntityProjectileBase) {
                        if (TConstruct.pulseManager.isPulseLoaded(TinkerTools.PulseId)) {
                            ItemStack stack = ((EntityProjectileBase) immediateSource).tinkerProjectile.getItemStack();
                            if (TinkerUtil.hasTrait(TagUtil.getTagSafe(stack), TinkerTraits.holy.getIdentifier()) || TinkerUtil.hasModifier(TagUtil.getTagSafe(stack), TinkerModifiers.modSmite.getIdentifier())) {
                                return true;
                            }
                        }
                    }
                }

                //Reliquary's Holy Hand Grenade
                if (Loader.isModLoaded("xreliquary") && immediateSource instanceof EntityHolyHandGrenade) {
                    return true;
                }
                
                //Heat and climate's Silver Dagger
                if (Loader.isModLoaded("dcs_climate") && immediateSource instanceof EntityProjSilver) {
                    return true;
                }
				//Heat and climate's Silver Bullets
                if (Loader.isModLoaded("dcs_climate") && immediateSource instanceof EntitySilverBullet) {
                    return true;
                }
            }

            return false;
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

    private void cancelUndeath(LivingDeathEvent evt) {

        EntityLivingBase entity = evt.getEntityLiving();
        evt.setCanceled(true);
        entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 1200, 2));
        entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 1200, 2));
        entity.setHealth(1);
    }

    private boolean isSilverMaterial(String materialName) {

        if (materialName != null) {
            materialName = materialName.toLowerCase();
            return materialName.contains("silver");
        }

        return false;
    }

    private boolean isNaturalDamage(DamageSource source) {
        return source == DamageSource.IN_WALL || source == DamageSource.CRAMMING || source == DamageSource.OUT_OF_WORLD;
    }

    @Optional.Method(modid = "gamestages")
    public boolean hasRequiredStage(EntityPlayer player) {
        boolean flag = ConfigHandler.gameStageMode == 0;
        Collection<String> unlockedStages = PlayerDataHandler.getStageData(player).getUnlockedStages();
        if (ConfigHandler.unlockStage.length == 0) {
            return true;
        } else {
            boolean flag2 = false;
            boolean flag3 = true;
            for (String stage : ConfigHandler.unlockStage) {
                if (flag && unlockedStages.contains(stage)) {
                    flag2 = true;
                    break;
                }
                if (!flag && !unlockedStages.contains(stage)) {
                    flag3 = false;
                    break;
                }
            }
            return (flag && flag2) || (!flag && flag3);
        }
    }

    public boolean isAllowedDimension(EntityLivingBase entity) {
        if (ConfigHandler.dimensionsList.isEmpty()) {
            return true;
        } else if (ConfigHandler.dimensionMode == 0) {
            return !ConfigHandler.dimensionsList.contains(entity.dimension);
        } else {
            return ConfigHandler.dimensionsList.contains(entity.dimension);
        }
    }

    public boolean isHonoraryUndead(EntityLivingBase entity) {
        for (String s : ConfigHandler.undeadMobs) {
            ResourceLocation rl = EntityList.getKey(entity);
            if (rl != null && s.equals(rl.toString())) {
                return true;
            }
        }
        return false;
    }

    @Optional.Method(modid = "toolbox")
    private boolean isSilverToolbox(ItemStack stack) {
        Item item = stack.getItem();
        List<PartMaterial> materials = new ArrayList<>();
        if (item instanceof IHeadTool) {
            materials.add(IHeadTool.getHeadMat(stack));
        }
        if (item instanceof IBladeTool) {
            materials.add(IBladeTool.getBladeMat(stack));
        }
        if (item instanceof ICrossguardTool) {
            materials.add(ICrossguardTool.getCrossguardMat(stack));
        }
        if (item instanceof IHaftTool) {
            materials.add(IHaftTool.getHaftMat(stack));
        }
        if (item instanceof IHandleTool) {
            materials.add(IHandleTool.getHandleMat(stack));
        }
        if (item instanceof IAdornedTool) {
            materials.add(IAdornedTool.getAdornmentMat(stack));
        }

        for (PartMaterial mat : materials) {
            if (isSilverMaterial(mat.getName())) {
                return true;
            }
        }

        return false;
    }
}
