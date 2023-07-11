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
