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

import com.illusivesoulworks.consecration.api.IUndying;
import com.illusivesoulworks.consecration.common.capability.ConsecrationComponents;
import com.illusivesoulworks.consecration.platform.services.ICapabilityService;
import java.util.Optional;
import net.minecraft.world.entity.LivingEntity;

public class FabricCapabilityService implements ICapabilityService {

  @Override
  public Optional<? extends IUndying> getUndying(LivingEntity livingEntity) {
    return ConsecrationComponents.UNDYING.maybeGet(livingEntity);
  }
}
