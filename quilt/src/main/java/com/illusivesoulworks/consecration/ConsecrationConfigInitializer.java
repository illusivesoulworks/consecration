package com.illusivesoulworks.consecration;

import com.illusivesoulworks.consecration.common.config.ConsecrationConfig;
import com.illusivesoulworks.spectrelib.config.SpectreConfigInitializer;
import org.quiltmc.loader.api.ModContainer;

public class ConsecrationConfigInitializer implements SpectreConfigInitializer {

  @Override
  public void onInitializeConfig(ModContainer modContainer) {
    ConsecrationConfig.setup();
  }

  // Hotfix to avoid crashing when loaded with the Fabric-version of SpectreLib
  public void onInitializeConfig() {
    onInitializeConfig(null);
  }
}
