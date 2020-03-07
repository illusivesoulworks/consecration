package top.theillusivec4.consecration.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import org.apache.commons.lang3.tuple.Pair;
import top.theillusivec4.consecration.Consecration;

public class ConsecrationConfig {

  public static final ForgeConfigSpec serverSpec;
  public static final Server SERVER;
  private static final String CONFIG_PREFIX = "gui." + Consecration.MODID + ".config.";

  static {
    final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder()
        .configure(Server::new);
    serverSpec = specPair.getRight();
    SERVER = specPair.getLeft();
  }

  public enum PermissionMode {
    BLACKLIST, WHITELIST
  }

  public static class Server {

    public final ConfigValue<List<? extends Integer>> dimensions;
    public final ForgeConfigSpec.EnumValue<PermissionMode> dimensionPermission;

    public final ForgeConfigSpec.IntValue fireSmiteDuration;
    public final ForgeConfigSpec.IntValue holySmiteDuration;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> holyEntities;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> holyEffects;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> holyItems;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> holyEnchantments;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> holyDamage;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> holyMaterials;

    public final ForgeConfigSpec.BooleanValue defaultUndead;
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> undeadList;
    public final ForgeConfigSpec.DoubleValue damageReduction;
    public final ForgeConfigSpec.IntValue healthRegen;
    public final ForgeConfigSpec.DoubleValue speedModifier;
    public final ForgeConfigSpec.BooleanValue bystanderNerf;

    public Server(ForgeConfigSpec.Builder builder) {
      builder.push("dimension");

      dimensions = builder
          .comment("Set which dimensions are blacklisted or whitelisted for affected undead")
          .translation(CONFIG_PREFIX + "dimensions")
          .defineList("dimensions", ArrayList::new, x -> x instanceof Integer);

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
          .defineList("holyEntities", ArrayList::new, x -> x instanceof String);

      holyEffects = builder
          .comment("A list of potion effects that will be able to damage and smite undead")
          .translation(CONFIG_PREFIX + "holyEffects")
          .defineList("holyEffects", Arrays.asList("minecraft:instant_health", "consecration:holy"),
              x -> x instanceof String);

      holyItems = builder.comment("A list of items that will be able to damage and smite undead")
          .translation(CONFIG_PREFIX + "holyItems")
          .defineList("holyItems", ArrayList::new, x -> x instanceof String);

      holyEnchantments = builder
          .comment("A list of enchantments that will be able to damage and smite undead")
          .translation(CONFIG_PREFIX + "holyEnchantments")
          .defineList("holyEnchantments", Collections.singletonList("minecraft:smite"),
              x -> x instanceof String);

      holyDamage = builder
          .comment("A list of damage types that will be able to damage and smite undead")
          .translation(CONFIG_PREFIX + "holyDamage")
          .defineList("holyDamage", Collections.singletonList("holy"), x -> x instanceof String);

      holyMaterials = builder
          .comment("A list of materials that will be able to damage and smite undead")
          .translation(CONFIG_PREFIX + "holyMaterial")
          .defineList("holyMaterial", Collections.singletonList("silver"),
              x -> x instanceof String);

      builder.pop();

      builder.push("undying");

      defaultUndead = builder.comment("Set to true to give default undead the undying trait")
          .translation(CONFIG_PREFIX + "defaultUndead").define("defaultUndead", true);

      undeadList = builder.comment(
          "A list of mobs that to classify as undead." + "\nFormat: 'modid:name'"
              + "\nOptionally, add ';unholy' or ';absolute' to the end."
              + "\nUnholy mobs will not be smote by fire and absolute mobs will not be smote by anything.")
          .translation(CONFIG_PREFIX + "undeadList")
          .defineList("undeadList", ArrayList::new, x -> x instanceof String);

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
