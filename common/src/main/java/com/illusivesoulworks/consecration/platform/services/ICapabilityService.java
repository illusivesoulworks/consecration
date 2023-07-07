package com.illusivesoulworks.consecration.platform.services;

import com.illusivesoulworks.consecration.api.IUndying;
import java.util.Optional;
import net.minecraft.world.entity.LivingEntity;

public interface ICapabilityService {

  Optional<IUndying> getUndying(LivingEntity livingEntity);
}
