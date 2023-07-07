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
