package com.illusivesoulworks.consecration.platform;

import com.illusivesoulworks.consecration.ConsecrationConstants;
import com.illusivesoulworks.consecration.common.network.SPacketSetVulnerability;
import com.illusivesoulworks.consecration.platform.services.INetworkService;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.PlayerLookup;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;

public class QuiltNetworkService implements INetworkService {

  @Override
  public void sendVulnerabilitySyncS2C(LivingEntity livingEntity, int duration) {
    SPacketSetVulnerability msg = new SPacketSetVulnerability(livingEntity.getId(), duration);
    FriendlyByteBuf buf = PacketByteBufs.create();
    SPacketSetVulnerability.encode(msg, buf);
    PlayerLookup.tracking(livingEntity).forEach(
        serverPlayer -> ServerPlayNetworking.send(serverPlayer,
            new ResourceLocation(ConsecrationConstants.MOD_ID, "vulnerability"), buf));
  }
}
