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

import javax.annotation.Nonnull;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.consecration.common.capability.UndyingCapability;
import top.theillusivec4.consecration.common.capability.UndyingCapability.IUndying;
import top.theillusivec4.consecration.common.registry.RegistryReference;

import net.minecraft.world.item.enchantment.Enchantment.Rarity;

public class ShadowProtection extends Enchantment {

  public ShadowProtection() {
    super(Rarity.UNCOMMON, EnchantmentCategory.ARMOR,
        new EquipmentSlot[]{EquipmentSlot.CHEST, EquipmentSlot.FEET,
            EquipmentSlot.HEAD, EquipmentSlot.LEGS});
    this.setRegistryName(RegistryReference.SHADOW_PROTECTION);
  }

  public int getMinCost(int enchantmentLevel) {
    return 8 + (enchantmentLevel - 1) * 6;
  }

  public int getMaxCost(int enchantmentLevel) {
    return this.getMinCost(enchantmentLevel) + 6;
  }

  public int getMaxLevel() {
    return 4;
  }

  public int getDamageProtection(int level, DamageSource source) {

    if (source.isBypassInvul()) {
      return 0;
    }
    Entity entity = source.getEntity();

    if (entity instanceof LivingEntity) {
      LazyOptional<IUndying> undyingOpt = UndyingCapability.getCapability((LivingEntity) entity);
      return undyingOpt.map(undying -> level * 2).orElse(0);
    }
    return 0;
  }

  public boolean checkCompatibility(@Nonnull Enchantment enchantment) {

    if (enchantment instanceof ProtectionEnchantment protectionenchantment) {
      return protectionenchantment.type == ProtectionEnchantment.Type.FALL;
    }
    return super.checkCompatibility(enchantment);
  }
}
