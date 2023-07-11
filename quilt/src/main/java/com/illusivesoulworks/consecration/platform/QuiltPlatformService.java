package com.illusivesoulworks.consecration.platform;

import com.illusivesoulworks.consecration.platform.services.IPlatformService;
import org.quiltmc.loader.api.QuiltLoader;

public class QuiltPlatformService implements IPlatformService {

  @Override
  public String getPlatformName() {
    return "Quilt";
  }

  @Override
  public boolean isModLoaded(String modId) {
    return QuiltLoader.isModLoaded(modId);
  }

  @Override
  public boolean isDevelopmentEnvironment() {
    return QuiltLoader.isDevelopmentEnvironment();
  }
}
