/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.potions;

import c4.consecration.common.init.ConsecrationDamageSources;
import c4.consecration.common.util.UndeadHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HolyPotion extends Potion {

    public HolyPotion() {
        super(false, 0xffffff);
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    public void affectEntity(@Nullable Entity source, @Nullable Entity indirectSource,
                             @Nonnull EntityLivingBase entityLivingBaseIn, int amplifier, double health) {

        if (UndeadHelper.isUndead(entityLivingBaseIn)) {
            entityLivingBaseIn.attackEntityFrom(ConsecrationDamageSources.HOLY, (float)(8 << amplifier));
        } else {
            entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 600, amplifier));
            entityLivingBaseIn.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 600, amplifier));
        }
    }
}
