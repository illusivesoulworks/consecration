package com.illusivesoulworks.consecration;

import com.illusivesoulworks.consecration.common.network.SPacketSetVulnerability;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.resources.ResourceLocation;

public class ConsecrationFabricClientMod implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    ClientPlayNetworking.registerGlobalReceiver(
        new ResourceLocation(ConsecrationConstants.MOD_ID, "vulnerability"),
        (client, handler, buf, responseSender) -> {
          SPacketSetVulnerability msg = SPacketSetVulnerability.decode(buf);
          client.execute(() -> SPacketSetVulnerability.handle(msg));
        });
  }
}
