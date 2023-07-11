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

package com.illusivesoulworks.consecration.api;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

public abstract class ConsecrationApi {

  public static ConsecrationApi getInstance() {
    throw new RuntimeException("Consecration could not find an API instance!");
  }

  public abstract String getModId();

  public abstract String getHolyIdentifier();

  public abstract Optional<? extends IUndying> getUndying(LivingEntity livingEntity);

  public abstract boolean isHolyEntity(Entity entity);

  public abstract boolean isHolyItem(Item item);

  public abstract boolean isHolyItem(ItemStack stack);

  public abstract boolean isHolyEnchantment(Enchantment enchantment);

  public abstract boolean isHolyEffect(MobEffect mobEffect);

  public abstract boolean isHolyDamage(DamageSource damageSource);

  public boolean isHolyDamage(String damage) {
    return damage.equals("holy");
  }

  public abstract boolean isHolyMaterial(String material);

  public abstract int getHolyProtectionLevel(LivingEntity attacker,
                                             LivingEntity livingEntity, DamageSource damageSource);

  public abstract boolean isHolyAttack(LivingEntity livingEntity, DamageSource damageSource);

  public abstract DamageSource causeHolyDamage(@Nonnull Entity entity);

  public abstract DamageSource causeIndirectHolyDamage(@Nonnull Entity source,
                                                       @Nullable Entity indirect);

  public abstract DamageSource causeHolyDamage(RegistryAccess registryAccess);

  public abstract VulnerabilityType getVulnerabilityType(LivingEntity livingEntity,
                                                         DamageSource source);

  public abstract UndeadType getUndeadType(LivingEntity livingEntity);
}
