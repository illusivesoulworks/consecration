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
import com.illusivesoulworks.consecration.platform.services.ICapabilityService;
import com.illusivesoulworks.consecration.platform.services.INetworkService;
import com.illusivesoulworks.consecration.platform.services.IPlatformService;
import com.illusivesoulworks.consecration.platform.services.IRegistryService;
import java.util.ServiceLoader;

public class Services {

  public static final IRegistryService REGISTRY = load(IRegistryService.class);
  public static final IPlatformService PLATFORM = load(IPlatformService.class);
  public static final INetworkService NETWORK = load(INetworkService.class);
  public static final ICapabilityService CAPABILITY = load(ICapabilityService.class);

  public static <T> T load(Class<T> clazz) {

    final T loadedService = ServiceLoader.load(clazz)
        .findFirst()
        .orElseThrow(
            () -> new NullPointerException("Failed to load service for " + clazz.getName()));
    ConsecrationConstants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
    return loadedService;
  }
}
