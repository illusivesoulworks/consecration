package com.illusivesoulworks.consecration.common.network;

import com.illusivesoulworks.consecration.ConsecrationConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ConsecrationNetwork {

  private static final String PTC_VERSION = "1";

  public static SimpleChannel INSTANCE;

  private static int id = 0;

  public static void setup() {
    INSTANCE = NetworkRegistry.ChannelBuilder.named(
            new ResourceLocation(ConsecrationConstants.MOD_ID, "main"))
        .networkProtocolVersion(() -> PTC_VERSION).clientAcceptedVersions(PTC_VERSION::equals)
        .serverAcceptedVersions(PTC_VERSION::equals).simpleChannel();
    ConsecrationNetwork.INSTANCE.registerMessage(id++, SPacketSetVulnerability.class,
        SPacketSetVulnerability::encode, SPacketSetVulnerability::decode, (msg, ctx) -> {
          ctx.get().enqueueWork(() -> SPacketSetVulnerability.handle(msg));
          ctx.get().setPacketHandled(true);
        });
  }
}
