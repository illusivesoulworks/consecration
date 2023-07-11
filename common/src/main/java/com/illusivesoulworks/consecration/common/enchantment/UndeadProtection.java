/*
 * Copyright (C) 2017-2023 Illusive Soulworks
 *
 * Consecration is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Consecration is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Consecration.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.consecration.common.enchantment;

import com.illusivesoulworks.consecration.api.ConsecrationApi;
import javax.annotation.Nonnull;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;

public class UndeadProtection extends Enchantment {

  public UndeadProtection() {
    super(Rarity.UNCOMMON, EnchantmentCategory.ARMOR,
        new EquipmentSlot[] {EquipmentSlot.CHEST, EquipmentSlot.FEET, EquipmentSlot.HEAD,
            EquipmentSlot.LEGS});
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

    if (entity instanceof LivingEntity livingEntity) {
      return ConsecrationApi.getInstance().getUndying(livingEntity).map(undying -> level * 2)
          .orElse(0);
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
