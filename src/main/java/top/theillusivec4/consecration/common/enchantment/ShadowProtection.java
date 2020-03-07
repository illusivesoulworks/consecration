/*
 * Copyright (c) 2017-2020 C4
 *
 * This file is part of Consecration, a mod made for Minecraft.
 *
 * Consecration is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Consecration is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Consecration.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.consecration.common.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.consecration.common.capability.UndyingCapability;
import top.theillusivec4.consecration.common.capability.UndyingCapability.IUndying;
import top.theillusivec4.consecration.common.registry.RegistryReference;

public class ShadowProtection extends Enchantment {

  public ShadowProtection() {
    super(Rarity.UNCOMMON, EnchantmentType.ARMOR,
        new EquipmentSlotType[]{EquipmentSlotType.CHEST, EquipmentSlotType.FEET,
            EquipmentSlotType.HEAD, EquipmentSlotType.LEGS});
    this.setRegistryName(RegistryReference.SHADOW_PROTECTION);
  }

  public int getMinEnchantability(int enchantmentLevel) {
    return 8 + (enchantmentLevel - 1) * 6;
  }

  public int getMaxEnchantability(int enchantmentLevel) {
    return this.getMinEnchantability(enchantmentLevel) + 6;
  }

  public int getMaxLevel() {
    return 4;
  }

  public int calcModifierDamage(int level, DamageSource source) {

    if (source.canHarmInCreative()) {
      return 0;
    }
    Entity entity = source.getTrueSource();

    if (entity instanceof LivingEntity) {
      LazyOptional<IUndying> undyingOpt = UndyingCapability.getCapability((LivingEntity) entity);
      return undyingOpt.map(undying -> level * 2).orElse(0);
    }
    return 0;
  }

  public boolean canApplyTogether(Enchantment ench) {

    if (ench instanceof ProtectionEnchantment) {
      ProtectionEnchantment protectionenchantment = (ProtectionEnchantment) ench;
      return protectionenchantment.protectionType == ProtectionEnchantment.Type.FALL;
    }
    return super.canApplyTogether(ench);
  }
}
