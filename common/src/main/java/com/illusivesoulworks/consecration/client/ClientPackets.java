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
