package com.illusivesoulworks.consecration.platform.services;

import net.minecraft.world.entity.LivingEntity;

public interface INetworkService {

  void sendVulnerabilitySyncS2C(LivingEntity livingEntity, int duration);
}
