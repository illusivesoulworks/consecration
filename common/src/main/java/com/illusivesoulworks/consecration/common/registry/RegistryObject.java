package com.illusivesoulworks.consecration.common.registry;

import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public interface RegistryObject<T> extends Supplier<T> {

  ResourceKey<T> getResourceKey();

  ResourceLocation getId();

  @Override
  T get();

  Holder<T> asHolder();
}
