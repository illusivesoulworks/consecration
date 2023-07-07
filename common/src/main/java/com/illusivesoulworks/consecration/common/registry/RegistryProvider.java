package com.illusivesoulworks.consecration.common.registry;

import com.illusivesoulworks.consecration.platform.Services;
import java.util.Collection;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface RegistryProvider<T> {

  static <T> RegistryProvider<T> get(ResourceKey<? extends Registry<T>> resourceKey, String modId) {
    return Services.REGISTRY.create(resourceKey, modId);
  }

  static <T> RegistryProvider<T> get(Registry<T> registry, String modId) {
    return Services.REGISTRY.create(registry, modId);
  }

  <I extends T> RegistryObject<I> register(String name, Supplier<? extends I> supplier);

  Collection<RegistryObject<T>> getEntries();

  String getModId();
}
