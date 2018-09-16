/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.config;

import c4.consecration.Consecration;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.*;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@Config(modid = Consecration.MODID)
public class HandlerConfig {

    @Name("Include as Undead Mob")
    @Comment("A list of mobs that will be classified as undead by this mod in addition to the regular undead")
    public static String[] undeadMobs = new String[]{"minecraft:villager_golem"};

    @Name("Dimension Permission Mode")
    @Comment("Set whether the dimension configuration is blacklisted or whitelisted")
    public static PermissionMode dimensionPermission = PermissionMode.BLACKLIST;

    @Name("Dimension List")
    @Comment("Set which dimensions are blacklisted or whitelisted for affected undead, leave blank to disable this " +
            "option")
    public static String[] dimensionList = new String[]{};

    public static Undying undying = new Undying();

    public static class Undying {

        @Name("Damage Reduction")
        @Comment("Set undead natural damage reduction, in percent, against all non-holy damage")
        @RangeDouble(min = 0.0D, max = 1.0D)
        public double damageReduction = 0.8D;

        @Name("Health Regen")
        @Comment("Set undead natural health regen, in half-hearts per second")
        @RangeInt(min = 0, max = 1000)
        public int healthRegen = 1;
    }

    public enum PermissionMode {
        BLACKLIST,
        WHITELIST;
    }
}
