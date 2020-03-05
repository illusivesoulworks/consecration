package top.theillusivec4.consecration.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;
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

  public static class Server {

    public final ForgeConfigSpec.ConfigValue<List<Integer>> dimensions;
    public final ForgeConfigSpec.EnumValue<PermissionMode> dimensionPermission;

    public final ForgeConfigSpec.IntValue fireSmiteDuration;
    public final ForgeConfigSpec.IntValue holySmiteDuration;
    public final ForgeConfigSpec.ConfigValue<List<String>> holyEntities;
    public final ForgeConfigSpec.ConfigValue<List<String>> holyPotions;
    public final ForgeConfigSpec.ConfigValue<List<String>> holyWeapons;
    public final ForgeConfigSpec.ConfigValue<List<String>> holyEnchantments;
    public final ForgeConfigSpec.ConfigValue<List<String>> holyDamage;
    public final ForgeConfigSpec.ConfigValue<List<String>> holyMaterials;

    public final ForgeConfigSpec.BooleanValue defaultUndead;
    public final ForgeConfigSpec.ConfigValue<List<String>> undeadList;
    public final ForgeConfigSpec.DoubleValue damageReduction;
    public final ForgeConfigSpec.IntValue healthRegen;
    public final ForgeConfigSpec.DoubleValue speedModifier;
    public final ForgeConfigSpec.BooleanValue bystanderNerf;

    public Server(ForgeConfigSpec.Builder builder) {
      builder.push("dimension");

      dimensions = builder
          .comment("Set which dimensions are blacklisted or whitelisted for affected undead")
          .translation(CONFIG_PREFIX + "dimensions").define("dimensions", new ArrayList<>());

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
          .translation(CONFIG_PREFIX + "holyEntities").define("holyEntities", new ArrayList<>());

      holyPotions = builder
          .comment("A list of potions that will be able to damage and smite undead")
          .translation(CONFIG_PREFIX + "holyPotions").define("holyPotions", new ArrayList<>());

      holyWeapons = builder.comment("A list of items that will be able to damage and smite undead")
          .translation(CONFIG_PREFIX + "holyWeapons").define("holyWeapons", new ArrayList<>());

      holyEnchantments = builder
          .comment("A list of enchantments that will be able to damage and smite undead")
          .translation(CONFIG_PREFIX + "holyEnchantments").define("holyEnchantments",
              new ArrayList<>(Collections.singletonList("minecraft:smite")));

      holyDamage = builder
          .comment("A list of damage types that will be able to damage and smite undead")
          .translation(CONFIG_PREFIX + "holyDamage")
          .define("holyDamage", new ArrayList<>(Collections.singletonList("holy")));

      holyMaterials = builder
          .comment("A list of materials that will be able to damage and smite undead")
          .translation(CONFIG_PREFIX + "holyMaterial")
          .define("holyMaterial", new ArrayList<>(Collections.singletonList("silver")));

      builder.pop();

      builder.push("undying");

      defaultUndead = builder.comment("Set to true to give default undead the undying trait")
          .translation(CONFIG_PREFIX + "defaultUndead").define("defaultUndead", true);

      undeadList = builder.comment(
          "A list of mobs that to classify as undead." + "\nFormat: 'modid:name'"
              + "\nOptionally, add ';unholy' or ';absolute' to the end."
              + "\nUnholy mobs will not be smote by fire and absolute mobs will not be smote by anything.")
          .translation(CONFIG_PREFIX + "undeadList").define("undeadList",
              new ArrayList<>(Collections.singletonList("minecraft:witch;unholy")));

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

  public enum PermissionMode {
    BLACKLIST, WHITELIST
  }
}
