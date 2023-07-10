package com.illusivesoulworks.consecration.platform.services;

import com.illusivesoulworks.consecration.api.IUndying;
import com.illusivesoulworks.consecration.common.capability.UndyingCapability;
import java.util.Optional;
import net.minecraft.world.entity.LivingEntity;

public class ForgeCapabilityService implements ICapabilityService {

  @Override
  public Optional<? extends IUndying> getUndying(LivingEntity livingEntity) {
    return livingEntity.getCapability(UndyingCapability.INSTANCE).resolve();
  }
}
