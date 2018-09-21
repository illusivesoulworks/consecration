/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.enchantments;

import c4.consecration.Consecration;
import c4.consecration.common.capabilities.CapabilityUndying;
import c4.consecration.common.capabilities.IUndying;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import javax.annotation.Nonnull;

public class EnchantmentBlessing extends Enchantment {

    public EnchantmentBlessing() {
        super(Rarity.UNCOMMON, EnumEnchantmentType.ARMOR, new EntityEquipmentSlot[]{
                EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET});
        this.setName(Consecration.MODID + ".blessing");
        this.setRegistryName(Consecration.MODID, "blessing");
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 3 + (enchantmentLevel - 1) * 8;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return this.getMinEnchantability(enchantmentLevel) + 8;
    }

    @Override
    public int getMaxLevel()
    {
        return 4;
    }

    @Override
    public int calcModifierDamage(int level, DamageSource source) {
        if (source.canHarmInCreative()) {
            return 0;
        } else if (source.getTrueSource() instanceof EntityLivingBase){
            IUndying undying = CapabilityUndying.getUndying((EntityLivingBase)source.getTrueSource());
            return undying != null ? level * 2 : 0;
        } else {
            return 0;
        }
    }

    @Override
    public boolean canApplyTogether(Enchantment ench) {
        if (ench instanceof EnchantmentProtection) {
            return ((EnchantmentProtection)ench).protectionType == EnchantmentProtection.Type.FALL;
        } else {
            return super.canApplyTogether(ench);
        }
    }
}
