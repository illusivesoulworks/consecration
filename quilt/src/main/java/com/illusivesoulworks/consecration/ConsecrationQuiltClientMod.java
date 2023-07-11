package com.illusivesoulworks.consecration;

import com.illusivesoulworks.consecration.common.network.SPacketSetVulnerability;
import net.minecraft.resources.ResourceLocation;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

public class ConsecrationQuiltClientMod implements ClientModInitializer {

  @Override
  public void onInitializeClient(ModContainer modContainer) {
    ClientPlayNetworking.registerGlobalReceiver(
        new ResourceLocation(ConsecrationConstants.MOD_ID, "vulnerability"),
        (client, handler, buf, responseSender) -> {
          SPacketSetVulnerability msg = SPacketSetVulnerability.decode(buf);
          client.execute(() -> SPacketSetVulnerability.handle(msg));
        });
  }
}
