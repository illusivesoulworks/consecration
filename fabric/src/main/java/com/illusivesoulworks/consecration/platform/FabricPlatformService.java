package com.illusivesoulworks.consecration.platform;

import com.illusivesoulworks.consecration.platform.services.IPlatformService;
import net.fabricmc.loader.api.FabricLoader;

public class FabricPlatformService implements IPlatformService {

  @Override
  public String getPlatformName() {
    return "Fabric";
  }

  @Override
  public boolean isModLoaded(String modId) {
    return FabricLoader.getInstance().isModLoaded(modId);
  }

  @Override
  public boolean isDevelopmentEnvironment() {
    return FabricLoader.getInstance().isDevelopmentEnvironment();
  }
}
