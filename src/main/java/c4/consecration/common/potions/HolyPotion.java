/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.potions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntityDamageSourceIndirect;

import javax.annotation.Nullable;

public class HolyPotion extends Potion {

    public HolyPotion() {
        super(false, 0xffffff);
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void affectEntity(@Nullable Entity source, @Nullable Entity indirectSource, EntityLivingBase entityLivingBaseIn, int amplifier, double health)
    {
        if (entityLivingBaseIn.isEntityUndead()) {

            int j = (int)(health * (double)(8 << amplifier));

            if (source == null) {
                entityLivingBaseIn.attackEntityFrom(ModPotions.HOLY_DAMAGE, (float) j);
            } else {
                entityLivingBaseIn.attackEntityFrom(new EntityDamageSourceIndirect(ModPotions.HOLY_DAMAGE.damageType, source, indirectSource).setDamageBypassesArmor().setMagicDamage(), (float) j);
            }
        } else {

            if (amplifier == 0) {
                entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100));
                entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 600));
            } else if (amplifier == 1) {
                entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200));
                entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 800));
                entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 1000));
            } else if (amplifier == 2) {
                entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 300));
                entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 1000));
                entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 1200));
                entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 1200));
            }
        }
    }
}
