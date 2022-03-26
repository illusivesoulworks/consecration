/*
 * Copyright (c) 2017-2020 C4
 *
 * This file is part of Consecration, a mod made for Minecraft.
 *
 * Consecration is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Consecration is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Consecration.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.consecration.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import org.apache.commons.lang3.tuple.Pair;
import top.theillusivec4.consecration.api.ConsecrationApi;

public class ConsecrationConfig {

  public static final ForgeConfigSpec CONFIG_SPEC;
  public static final Config CONFIG;
  private static final String CONFIG_PREFIX = "gui." + ConsecrationApi.MOD_ID + ".config.";

  static {
    final Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder()
        .configure(Config::new);
    CONFIG_SPEC = specPair.getRight();
    CONFIG = specPair.getLeft();
  }

  public enum PermissionMode {
    BLACKLIST, WHITELIST
  }

  public static Set<ResourceLocation> dimensions;
  public static PermissionMode dimensionsPermission;

  public static int fireVulnerableDuration;
  public static int holyVulnerableDuration;

  public static List<? extends String> holyDamage;
  public static List<? extends String> holyMaterials;

  public static boolean giveDefaultUndeadUndying;
  public static double damageReduction;
  public static double healthRegeneration;
  public static double speedModifier;
  public static double damageReductionVsMobs;

  public static void bake() {
    dimensions = new HashSet<>();
    CONFIG.dimensions.get().forEach(dimension -> dimensions.add(new ResourceLocation(dimension)));
    dimensionsPermission = CONFIG.dimensionsPermission.get();

    fireVulnerableDuration = CONFIG.fireVulnerableDuration.get();
    holyVulnerableDuration = CONFIG.holyVulnerableDuration.get();

    holyDamage = CONFIG.holyDamage.get();
    holyMaterials = CONFIG.holyMaterials.get();

    giveDefaultUndeadUndying = CONFIG.giveDefaultUndeadUndying.get();
    damageReduction = CONFIG.damageReduction.get();
    healthRegeneration = CONFIG.healthRegeneration.get();
    speedModifier = CONFIG.speedModifier.get();
    damageReductionVsMobs = CONFIG.damageReductionVsMobs.get();
  }

  public static class Config {

    public final ConfigValue<List<? extends String>> dimensions;
    public final EnumValue<PermissionMode> dimensionsPermission;

    public final IntValue fireVulnerableDuration;
    public final IntValue holyVulnerableDuration;
    public final ConfigValue<List<? extends String>> holyDamage;
    public final ConfigValue<List<? extends String>> holyMaterials;

    public final BooleanValue giveDefaultUndeadUndying;
    public final DoubleValue damageReduction;
    public final IntValue healthRegeneration;
    public final DoubleValue speedModifier;
    public final DoubleValue damageReductionVsMobs;

    public Config(ForgeConfigSpec.Builder builder) {
      builder.push("dimension");

      dimensions = builder
          .comment("Set which dimensions are blacklisted or whitelisted for empowered undead")
          .translation(CONFIG_PREFIX + "dimensions")
          .defineList("dimensions", ArrayList::new, s -> s instanceof String);

      dimensionsPermission = builder
          .comment("Set whether the dimension configuration is blacklisted or whitelisted")
          .translation(CONFIG_PREFIX + "dimensionsPermission")
          .defineEnum("dimensionsPermission", PermissionMode.BLACKLIST);

      builder.pop();

      builder.push("vulnerability");

      fireVulnerableDuration = builder
          .comment("The amount of time, in seconds, that vulnerability from fire lasts")
          .translation(CONFIG_PREFIX + "fireWeakeningDuration")
          .defineInRange("fireWeakeningDuration", 10, 0, 100);

      holyVulnerableDuration = builder
          .comment("The amount of time, in seconds, that vulnerability from holy sources lasts")
          .translation(CONFIG_PREFIX + "holyWeakeningDuration")
          .defineInRange("holyWeakeningDuration", 10, 0, 100);

      holyDamage = builder
          .comment("A list of damage types that will be able to damage and make undead vulnerable")
          .translation(CONFIG_PREFIX + "holyDamage")
          .defineList("holyDamage", Collections.singletonList("holy"), s -> s instanceof String);

      holyMaterials = builder
          .comment("A list of materials that will be able to damage and make undead vulnerable")
          .translation(CONFIG_PREFIX + "holyMaterials")
          .defineList("holyMaterials", Collections.singletonList("silver"),
              s -> s instanceof String);

      builder.pop();

      builder.push("undying");

      giveDefaultUndeadUndying =
          builder.comment("Set to true to give default undead creatures the undying trait")
              .translation(CONFIG_PREFIX + "giveDefaultUndeadUndying")
              .define("giveDefaultUndeadUndying", true);

      damageReduction = builder
          .comment("Set undead natural damage reduction, in percent, against non-vulnerable damage")
          .translation(CONFIG_PREFIX + "damageReduction")
          .defineInRange("damageReduction", 0.8D, 0.0D, 1.0D);

      healthRegeneration =
          builder.comment("Set undead natural health regeneration in half-hearts per second")
              .translation(CONFIG_PREFIX + "healthRegeneration")
              .defineInRange("healthRegeneration", 1, 0, 1000);

      speedModifier = builder.comment("Set undead natural bonus speed modifier")
          .translation(CONFIG_PREFIX + "speedModifier")
          .defineInRange("speedModifier", 0.0D, 0.0D, 100.0D);

      damageReductionVsMobs = builder
          .comment("Set undead natural damage reduction, in percent, against non-vulnerable mob damage")
          .translation(CONFIG_PREFIX + "damageReductionVsMobs")
          .defineInRange("damageReductionVsMobs", 0.8D, 0.0D, 1.0D);

      builder.pop();
    }
  }
}
