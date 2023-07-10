package com.illusivesoulworks.consecration.client;

import com.illusivesoulworks.consecration.api.ConsecrationApi;
import com.illusivesoulworks.consecration.common.network.SPacketSetVulnerability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ClientPackets {

  public static void handle(SPacketSetVulnerability msg) {
    ClientLevel level = Minecraft.getInstance().level;

    if (level != null) {
      Entity entity = level.getEntity(msg.entityId());

      if (entity instanceof LivingEntity livingEntity) {
        ConsecrationApi.getInstance().getUndying(livingEntity)
            .ifPresent(undying -> undying.setVulnerableDuration(msg.duration()));
      }
    }
  }
}
