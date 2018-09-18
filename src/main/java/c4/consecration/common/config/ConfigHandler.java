/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.config;

import c4.consecration.Consecration;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.*;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Consecration.MODID)
public class ConfigHandler {

    @Name("Dimension Permission Mode")
    @Comment("Set whether the dimension configuration is blacklisted or whitelisted")
    @RequiresMcRestart
    public static PermissionMode dimensionPermission = PermissionMode.BLACKLIST;

    @Name("Dimension List")
    @Comment("Set which dimensions are blacklisted or whitelisted for affected undead, leave blank to disable this " +
            "option")
    @RequiresMcRestart
    public static String[] dimensionList = new String[]{};

    public static Holy holy = new Holy();
    public static Undying undying = new Undying();

    public static class Holy {

        @Name("Holy Entities")
        @Comment("A list of entities that will be able to damage and smite undead")
        @RequiresMcRestart
        public String[] holyEntities = new String[]{"minecraft:villager_golem"};

        @Name("Holy Potions")
        @Comment("A list of potions that will be able to damage and smite undead")
        @RequiresMcRestart
        public String[] holyPotions = new String[]{"consecration:holy_potion"};

        @Name("Holy Weapons")
        @Comment("A list of items that will be able to damage and smite undead")
        @RequiresMcRestart
        public String[] holyWeapons = new String[]{};

        @Name("Holy Enchantments")
        @Comment("A list of enchantments that will be able to damage and smite undead")
        @RequiresMcRestart
        public String[] holyEnchantments = new String[]{"minecraft:smite"};

        @Name("Holy Damage")
        @Comment("A list of damage types that will be able to damage and smite undead")
        @RequiresMcRestart
        public String[] holyDamage = new String[]{"holy"};

        @Name("Holy Material")
        @Comment("A list of material names that will be able to damage and smite undead")
        @RequiresMcRestart
        public String[] holyMaterial = new String[]{"silver"};
    }

    public static class Undying {

        @Name("Include as Undead Mob")
        @Comment("A list of mobs that will be classified as undead by this mod in addition to the regular undead")
        @RequiresMcRestart
        public String[] undeadMobs = new String[]{};

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

    @Mod.EventBusSubscriber(modid = Consecration.MODID)
    private static class ConfigEventHandler {

        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent evt) {
            if (evt.getModID().equals(Consecration.MODID)) {
                ConfigManager.sync(Consecration.MODID, Config.Type.INSTANCE);
            }
        }
    }
}
