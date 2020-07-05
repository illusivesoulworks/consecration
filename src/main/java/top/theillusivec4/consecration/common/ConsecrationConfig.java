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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import org.apache.commons.lang3.tuple.Pair;
import top.theillusivec4.consecration.Consecration;

public class ConsecrationConfig {

  public static final ForgeConfigSpec CONFIG_SPEC;
  public static final Config CONFIG;
  private static final String CONFIG_PREFIX = "gui." + Consecration.MODID + ".config.";

  static {
    final Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder()
        .configure(Config::new);
    CONFIG_SPEC = specPair.getRight();
    CONFIG = specPair.getLeft();
  }

  public enum PermissionMode {
    BLACKLIST, WHITELIST
  }

  public static class Config {

    public final ConfigValue<List<? extends String>> dimensions;
    public final EnumValue<PermissionMode> dimensionPermission;

    public final IntValue fireSmiteDuration;
    public final IntValue holySmiteDuration;
    public final ConfigValue<List<? extends String>> holyEntities;
    public final ConfigValue<List<? extends String>> holyEffects;
    public final ConfigValue<List<? extends String>> holyItems;
    public final ConfigValue<List<? extends String>> holyEnchantments;
    public final ConfigValue<List<? extends String>> holyDamage;
    public final ConfigValue<List<? extends String>> holyMaterials;

    public final BooleanValue defaultUndead;
    public final ConfigValue<List<? extends String>> undeadList;
    public final DoubleValue damageReduction;
    public final IntValue healthRegen;
    public final DoubleValue speedModifier;
    public final BooleanValue bystanderNerf;

    public Config(ForgeConfigSpec.Builder builder) {
      builder.push("dimension");

      dimensions = builder
          .comment("Set which dimensions are blacklisted or whitelisted for affected undead")
          .translation(CONFIG_PREFIX + "dimensions")
          .defineList("dimensions", ArrayList::new, s -> s instanceof String);

      dimensionPermission = builder
          .comment("Set whether the dimension configuration is blacklisted or whitelisted")
          .translation(CONFIG_PREFIX + "dimensionPermission")
          .defineEnum("dimensionPermission", PermissionMode.BLACKLIST);

      builder.pop();

      builder.push("holy");

      fireSmiteDuration = builder
          .comment("The amount of time, in seconds, that smiting from fire lasts")
          .translation(CONFIG_PREFIX + "fireSmiteDuration")
          .defineInRange("fireSmiteDuration", 10, 0, 100);

      holySmiteDuration = builder
          .comment("The amount of time, in seconds, that smiting from holy sources lasts")
          .translation(CONFIG_PREFIX + "holySmiteDuration")
          .defineInRange("holySmiteDuration", 10, 0, 100);

      holyEntities = builder
          .comment("A list of entities that will be able to damage and smite undead")
          .translation(CONFIG_PREFIX + "holyEntities")
          .defineList("holyEntities", ArrayList::new, s -> s instanceof String);

      holyEffects = builder
          .comment("A list of potion effects that will be able to damage and smite undead")
          .translation(CONFIG_PREFIX + "holyEffects")
          .defineList("holyEffects", Arrays.asList("minecraft:instant_health", "consecration:holy"),
              s -> s instanceof String);

      holyItems = builder.comment("A list of items that will be able to damage and smite undead")
          .translation(CONFIG_PREFIX + "holyItems")
          .defineList("holyItems", ArrayList::new, s -> s instanceof String);

      holyEnchantments = builder
          .comment("A list of enchantments that will be able to damage and smite undead")
          .translation(CONFIG_PREFIX + "holyEnchantments")
          .defineList("holyEnchantments", Collections.singletonList("minecraft:smite"),
              s -> s instanceof String);

      holyDamage = builder
          .comment("A list of damage types that will be able to damage and smite undead")
          .translation(CONFIG_PREFIX + "holyDamage")
          .defineList("holyDamage", Collections.singletonList("holy"), s -> s instanceof String);

      holyMaterials = builder
          .comment("A list of materials that will be able to damage and smite undead")
          .translation(CONFIG_PREFIX + "holyMaterial")
          .defineList("holyMaterial", Collections.singletonList("silver"),
              s -> s instanceof String);

      builder.pop();

      builder.push("undying");

      defaultUndead = builder.comment("Set to true to give default undead the undying trait")
          .translation(CONFIG_PREFIX + "defaultUndead").define("defaultUndead", true);

      undeadList = builder.comment(
          "A list of mobs that to classify as undead." + "\nFormat: 'modid:name'"
              + "\nOptionally, add ';unholy' or ';absolute' to the end."
              + "\nUnholy mobs will not be smote by fire and absolute mobs will not be smote by anything.")
          .translation(CONFIG_PREFIX + "undeadList")
          .defineList("undeadList", ArrayList::new, s -> s instanceof String);

      damageReduction = builder
          .comment("Set undead natural damage reduction, in percent, against all non-holy damage")
          .translation(CONFIG_PREFIX + "damageReduction")
          .defineInRange("damageReduction", 0.8D, 0.0D, 1.0D);

      healthRegen = builder.comment("Set undead natural health regen, in half-hearts per second")
          .translation(CONFIG_PREFIX + "healthRegen").defineInRange("healthRegen", 1, 0, 1000);

      speedModifier = builder.comment("Set undead natural bonus speed modifier")
          .translation(CONFIG_PREFIX + "speedModifier")
          .defineInRange("speedModifier", 0.0D, 0.0D, 100.0D);

      bystanderNerf = builder
          .comment("Set to true to have undead reduce damage against non-player non-holy entities")
          .translation(CONFIG_PREFIX + "bystanderNerf").define("bystanderNerf", true);

      builder.pop();
    }
  }
}
