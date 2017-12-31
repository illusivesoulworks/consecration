/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common;

import c4.consecration.Consecration;
import c4.consecration.common.potions.ModPotions;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.Level;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.TinkerTraits;
import xreliquary.entities.EntityHolyHandGrenade;
import xreliquary.entities.shot.EntityExorcismShot;
import xreliquary.items.ItemHandgun;
import xreliquary.util.NBTHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.UUID;

public class EventHandlerCommon {

    private static Random rand = new Random();
    private static final Method START_CONVERTING = ReflectionHelper.findMethod(EntityZombieVillager.class, "startConverting", "func_191991_a", UUID.class, Integer.TYPE);

    @SubscribeEvent
    public void onPlayerAttack(AttackEntityEvent evt) {

        if (Loader.isModLoaded("survivalist")) {
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
    public void onUndeath(LivingDeathEvent evt) {

        if (!evt.getEntityLiving().getEntityWorld().isRemote) {

            if (evt.getEntityLiving().isEntityUndead()) {

                EntityLivingBase entity = evt.getEntityLiving();

                if (!evt.getSource().damageType.equals(ModPotions.HOLY_DAMAGE.damageType) && !isNonApplicableDamage(evt.getSource())) {

                    //Fire damage will still kill undead if they're not immune to fire
                    if (!entity.isImmuneToFire() && evt.getSource().isFireDamage()) {
                        return;
                    }

                    //Smite will still work also
                    boolean smite = false;
                    if (evt.getSource().getImmediateSource() instanceof EntityLivingBase) {
                        EntityLivingBase source = (EntityLivingBase) evt.getSource().getImmediateSource();
                        if (EnchantmentHelper.getModifierForCreature(source.getHeldItemMainhand(), entity.getCreatureAttribute()) > 0) {
                            smite = true;
                        } else if (Loader.isModLoaded("tconstruct")) {
                            if (TConstruct.pulseManager.isPulseLoaded(TinkerTools.PulseId)) {
                                if (TinkerUtil.hasTrait(TagUtil.getTagSafe(source.getHeldItemMainhand()), TinkerTraits.holy.getIdentifier())) {
                                    smite = true;
                                } else if (TinkerUtil.hasModifier(TagUtil.getTagSafe(source.getHeldItemMainhand()), TinkerModifiers.modSmite.getIdentifier())) {
                                    smite = true;
                                }
                            }
                        }
                    } else if (Loader.isModLoaded("xreliquary")) {
                        if (evt.getSource().getImmediateSource() instanceof EntityHolyHandGrenade) {
                            smite = true;
                        }
                    }

                    if (smite) {
                        return;
                    }

                    evt.setCanceled(true);
                    entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 1200, 2));
                    entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 1200, 2));
                    entity.setHealth(1);
                } else if (entity instanceof EntityZombieVillager) {
                    EntityZombieVillager zombievillager = (EntityZombieVillager) entity;

                    if (zombievillager.isConverting()) {
                        evt.setCanceled(true);
                        entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 1200, 2));
                        entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 1200, 2));
                        entity.setHealth(1);
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

    private static boolean isNonApplicableDamage(DamageSource source) {
        return source == DamageSource.IN_WALL || source == DamageSource.CRAMMING || source == DamageSource.OUT_OF_WORLD;
    }
}
