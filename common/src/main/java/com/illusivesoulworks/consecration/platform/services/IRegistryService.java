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

package com.illusivesoulworks.consecration.platform.services;

import com.illusivesoulworks.consecration.api.UndeadType;
import com.illusivesoulworks.consecration.common.registry.RegistryProvider;
import java.util.function.BiConsumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public interface IRegistryService {

  <T> RegistryProvider<T> create(ResourceKey<? extends Registry<T>> resourceKey, String modId);

  default <T> RegistryProvider<T> create(Registry<T> registry, String modId) {
    return create(registry.key(), modId);
  }

  DamageSource getDamageSource(String holy);

  void processUndeadTypes(BiConsumer<EntityType<?>, UndeadType> biConsumer);

  ResourceLocation getKey(Item item);

  boolean isHolyTag(Item item);

  boolean isHolyTag(EntityType<?> entity);

  boolean isHolyTag(MobEffect mobEffect);

  boolean isHolyTag(Enchantment enchantment);

  boolean canSmite(ItemStack stack);
}
