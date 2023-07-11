/*
 * Copyright (C) 2017-2023 Illusive Soulworks
 *
 * Consecration is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Consecration is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Consecration.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.consecration.platform;

import com.illusivesoulworks.consecration.ConsecrationConstants;
import com.illusivesoulworks.consecration.common.network.SPacketSetVulnerability;
import com.illusivesoulworks.consecration.platform.services.INetworkService;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class FabricNetworkService implements INetworkService {

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
