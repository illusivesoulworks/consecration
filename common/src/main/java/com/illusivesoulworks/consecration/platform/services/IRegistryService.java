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
