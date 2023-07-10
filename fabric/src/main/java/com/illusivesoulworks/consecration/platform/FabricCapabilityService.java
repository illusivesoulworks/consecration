package com.illusivesoulworks.consecration.platform;

import com.illusivesoulworks.consecration.api.IUndying;
import com.illusivesoulworks.consecration.common.capability.ConsecrationComponents;
import com.illusivesoulworks.consecration.platform.services.ICapabilityService;
import java.util.Optional;
import net.minecraft.world.entity.LivingEntity;

public class FabricCapabilityService implements ICapabilityService {

  @Override
  public Optional<? extends IUndying> getUndying(LivingEntity livingEntity) {
    return ConsecrationComponents.UNDYING.maybeGet(livingEntity);
  }
}
