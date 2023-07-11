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

package com.illusivesoulworks.consecration.common.config;

import com.illusivesoulworks.consecration.ConsecrationConstants;
import com.illusivesoulworks.consecration.common.impl.HolySources;
import com.illusivesoulworks.spectrelib.config.SpectreConfig;
import com.illusivesoulworks.spectrelib.config.SpectreConfigLoader;
import com.illusivesoulworks.spectrelib.config.SpectreConfigSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

public class ConsecrationConfig {

  public static final SpectreConfigSpec CONFIG_SPEC;
  public static final Config CONFIG;
  private static final String CONFIG_PREFIX = "gui." + ConsecrationConstants.MOD_ID + ".config.";

  static {
    final Pair<Config, SpectreConfigSpec> specPair = new SpectreConfigSpec.Builder()
        .configure(Config::new);
    CONFIG_SPEC = specPair.getRight();
    CONFIG = specPair.getLeft();
  }

  public enum ListType {
    DENY, ALLOW
  }

  public static void setup() {
    SpectreConfig config = SpectreConfigLoader.add(SpectreConfig.Type.SERVER, CONFIG_SPEC,
        ConsecrationConstants.MOD_ID);
    config.addLoadListener((cfg, flag) -> HolySources.reloadConfigs());
  }

  public static class Config {

    public final SpectreConfigSpec.ConfigValue<List<? extends String>> dimensionsList;
    public final SpectreConfigSpec.EnumValue<ListType> dimensionsListType;

    public final SpectreConfigSpec.IntValue fireVulnerableDuration;
    public final SpectreConfigSpec.IntValue holyVulnerableDuration;
    public final SpectreConfigSpec.ConfigValue<List<? extends String>> holyMaterials;

    public final SpectreConfigSpec.BooleanValue giveDefaultUndeadUndying;
    public final SpectreConfigSpec.IntValue damageReduction;
    public final SpectreConfigSpec.IntValue healthRegeneration;
    public final SpectreConfigSpec.IntValue speedModifier;
    public final SpectreConfigSpec.IntValue damageReductionVsMobs;

    public Config(SpectreConfigSpec.Builder builder) {
      builder.push("dimension");

      dimensionsList = builder
          .comment("Dimensions for empowered undead.")
          .translation(CONFIG_PREFIX + "dimensionsList")
          .defineList("dimensionsList", ArrayList::new, s -> s instanceof String);

      dimensionsListType = builder
          .comment("Determines if dimensionsList contains allowed dimensions or denied dimensions.")
          .translation(CONFIG_PREFIX + "dimensionsListType")
          .defineEnum("dimensionsListType", ListType.DENY);

      builder.pop();

      builder.push("vulnerability");

      fireVulnerableDuration = builder
          .comment("The number of seconds that vulnerability from fire lasts.")
          .translation(CONFIG_PREFIX + "fireVulnerableDuration")
          .defineInRange("fireVulnerableDuration", 10, 0, 100);

      holyVulnerableDuration = builder
          .comment("The number of seconds that vulnerability from holy sources lasts.")
          .translation(CONFIG_PREFIX + "holyVulnerableDuration")
          .defineInRange("holyVulnerableDuration", 10, 0, 100);

      holyMaterials = builder
          .comment("The materials that will be considered holy.")
          .translation(CONFIG_PREFIX + "holyMaterials")
          .defineList("holyMaterials", Collections.singletonList("silver"),
              s -> s instanceof String);

      builder.pop();

      builder.push("undying");

      giveDefaultUndeadUndying =
          builder.comment("If enabled, default undead creatures will be given the undying trait.")
              .translation(CONFIG_PREFIX + "giveDefaultUndeadUndying")
              .define("giveDefaultUndeadUndying", true);

      damageReduction = builder
          .comment("The percent damage reduction against non-vulnerable damage.")
          .translation(CONFIG_PREFIX + "damageReduction")
          .defineInRange("damageReduction", 80, 0, 100);

      healthRegeneration =
          builder.comment("The undying natural health regeneration in half-hearts per second.")
              .translation(CONFIG_PREFIX + "healthRegeneration")
              .defineInRange("healthRegeneration", 1, 0, 1000);

      speedModifier = builder.comment("The undying bonus speed modifier as a percent increase.")
          .translation(CONFIG_PREFIX + "speedModifier")
          .defineInRange("speedModifier", 0, 0, 1000);

      damageReductionVsMobs = builder
          .comment(
              "The percent damage reduction against non-vulnerable damage dealt by mobs.")
          .translation(CONFIG_PREFIX + "damageReductionVsMobs")
          .defineInRange("damageReductionVsMobs", 80, 0, 100);

      builder.pop();
    }
  }
}
