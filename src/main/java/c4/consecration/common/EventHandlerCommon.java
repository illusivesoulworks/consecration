/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common;

import c4.consecration.common.potions.ModPotions;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.TinkerTraits;

public class EventHandlerCommon {

    @SubscribeEvent
    public void onUndeath(LivingDeathEvent evt) {
        if (!evt.getEntityLiving().getEntityWorld().isRemote) {

            //The mob is undead and we're not dealing holy damage
            if (evt.getEntityLiving().isEntityUndead() && !evt.getSource().damageType.equals(ModPotions.HOLY_DAMAGE.getDamageType())) {

                EntityLivingBase entity = evt.getEntityLiving();

                //Fire damage will still kill undead if they're not immune to fire
                if (!entity.isImmuneToFire() && evt.getSource().isFireDamage()) {
                    return;
                }

                //Smite will still work also
                if (evt.getSource().getImmediateSource() instanceof EntityLivingBase) {
                    EntityLivingBase source = (EntityLivingBase) evt.getSource().getImmediateSource();
                    boolean smite = false;
                    if (EnchantmentHelper.getModifierForCreature(source.getHeldItemMainhand(), entity.getCreatureAttribute()) > 0) {
                        smite = true;
                    } else if (Loader.isModLoaded("tconstruct")) {
                        if (TConstruct.pulseManager.isPulseLoaded(TinkerTools.PulseId)) {
                            if (TinkerUtil.hasTrait(TagUtil.getTagSafe(source.getHeldItemMainhand()), TinkerTraits.holy.getIdentifier())) {
                                smite = true;
                            }
                        }
                    }
                    if (smite) {
                        return;
                    }
                }

                evt.setCanceled(true);
                entity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 1200, 2));
                entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 1200, 2));
                entity.setHealth(1);
            }
        }
    }
}
