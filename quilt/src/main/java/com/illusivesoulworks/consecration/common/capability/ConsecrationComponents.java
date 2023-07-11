package com.illusivesoulworks.consecration.common.capability;

import com.illusivesoulworks.consecration.ConsecrationConstants;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class ConsecrationComponents implements EntityComponentInitializer {

  public static final ComponentKey<UndyingComponent> UNDYING =
      ComponentRegistry.getOrCreate(new ResourceLocation(ConsecrationConstants.MOD_ID, "undying"),
          UndyingComponent.class);

  @Override
  public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
    registry.registerFor(LivingEntity.class, UNDYING, UndyingComponent::new);
  }
}
