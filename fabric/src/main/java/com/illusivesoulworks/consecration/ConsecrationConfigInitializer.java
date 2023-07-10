package com.illusivesoulworks.consecration;

import com.illusivesoulworks.consecration.common.config.ConsecrationConfig;
import com.illusivesoulworks.spectrelib.config.SpectreConfigInitializer;

public class ConsecrationConfigInitializer implements SpectreConfigInitializer {

  @Override
  public void onInitialize() {
    // NO-OP
  }

  @Override
  public void onInitializeConfig() {
    ConsecrationConfig.setup();
  }
}
