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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.potion.Potion;

import javax.annotation.Nonnull;

public class HolyPotion extends Potion {

    public HolyPotion() {
        super(false, 0xffffff);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        int k = 50 >> amplifier;

        if (k > 0) {
            return duration % k == 0;
        } else {
            return true;
        }
    }

    @Override
    public void performEffect(@Nonnull EntityLivingBase entityLivingBaseIn, int amplifier) {

        if (UndeadHelper.isUndead(entityLivingBaseIn)) {
            entityLivingBaseIn.attackEntityFrom(ConsecrationDamageSources.HOLY, (float)(2 << amplifier));
        } else if (entityLivingBaseIn.getHealth() < entityLivingBaseIn.getMaxHealth()) {
            entityLivingBaseIn.heal(1.0F);
        }
    }

    public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap
                                                    attributeMapIn, int amplifier) {
        if (!UndeadHelper.isUndead(entityLivingBaseIn)) {
            entityLivingBaseIn.setAbsorptionAmount(entityLivingBaseIn.getAbsorptionAmount() - (float) (4 * (amplifier + 1)));
        }
        super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
    }

    public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap
                                                 attributeMapIn, int amplifier) {
        if (!UndeadHelper.isUndead(entityLivingBaseIn)) {
            entityLivingBaseIn.setAbsorptionAmount(entityLivingBaseIn.getAbsorptionAmount() + (float) (4 * (amplifier + 1)));
        }
        super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
    }
}
