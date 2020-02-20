package top.theillusivec4.consecration.common;

import java.util.ArrayList;
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
    }
  }

  public enum PermissionMode {
    BLACKLIST, WHITELIST
  }
}
