package com.illusivesoulworks.consecration.platform.services;

import com.illusivesoulworks.consecration.common.network.SPacketSetVulnerability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.PacketDistributor;
import com.illusivesoulworks.consecration.common.network.ConsecrationNetwork;

public class ForgeNetworkService implements INetworkService {

  @Override
  public void sendVulnerabilitySyncS2C(LivingEntity livingEntity, int duration) {
    ConsecrationNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> livingEntity),
        new SPacketSetVulnerability(livingEntity.getId(), duration));
  }
}
