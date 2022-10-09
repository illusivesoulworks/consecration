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

package top.theillusivec4.consecration.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.util.LazyOptional;

public abstract class ConsecrationApi {

  public static final String HOLY_IDENTIFIER = "holy";
  public static final String MOD_ID = "consecration";

  private static ConsecrationApi INSTANCE;

  public static void setInstance(ConsecrationApi instance) {
    INSTANCE = instance;
  }

  public static ConsecrationApi getInstance() {
    return INSTANCE;
  }

  public abstract LazyOptional<IUndying> getUndying(LivingEntity livingEntity);

  public abstract boolean isHolyEntity(Entity entity);

  public abstract boolean isHolyItem(Item item);

  public abstract boolean isHolyItem(ItemStack stack);

  public abstract boolean isHolyEnchantment(Enchantment enchantment);

  public abstract boolean isHolyEffect(MobEffect mobEffect);

  public abstract boolean isHolyDamage(String damageType);

  public abstract boolean isHolyMaterial(String material);

  public abstract int getHolyProtectionLevel(LivingEntity attacker,
                                             LivingEntity livingEntity, DamageSource damageSource);

  public abstract boolean isHolyAttack(LivingEntity livingEntity, DamageSource damageSource);

  public abstract DamageSource causeHolyDamage(@Nonnull Entity entity);

  public abstract DamageSource causeIndirectHolyDamage(@Nonnull Entity source,
                                                       @Nullable Entity indirect);

  public abstract DamageSource causeHolyDamage();

  public abstract UndeadType getUndeadType(LivingEntity livingEntity);
}
