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

    @Name("Blessing XP Level Cost")
    @Comment("Set how many levels a blessing from a villager priest costs")
    public static int blessingCost = 1;

    public static Holy holy = new Holy();
    public static Undying undying = new Undying();
    public static ModSupport modSupport = new ModSupport();

    public static class Holy {

        @Name("Fire Smite Duration")
        @Comment("The amount of time, in seconds, that smiting from fire lasts")
        public int fireSmiteDuration = 5;

        @Name("Holy Smite Duration")
        @Comment("The amount of time, in seconds, that smiting from holy sources lasts")
        public int smiteDuration = 10;

        @Name("Holy Entities")
        @Comment("A list of entities that will be able to damage and smite undead")
        @RequiresMcRestart
        public String[] holyEntities = new String[]{"minecraft:villager_golem", "xreliquary:hand_grenade",
                "dcs_climate:dcs.main.bullet_silver"};

        @Name("Holy Potions")
        @Comment("A list of potions that will be able to damage and smite undead")
        @RequiresMcRestart
        public String[] holyPotions = new String[]{"consecration:holy_potion", "minecraft:instant_health", "bewitchment:holy_water"};

        @Name("Holy Weapons")
        @Comment("A list of items that will be able to damage and smite undead")
        @RequiresMcRestart
        public String[] holyWeapons = new String[]{"xreliquary:mercy_cross", "dcs_climate:dcs_dagger_silver"};

        @Name("Holy Enchantments")
        @Comment("A list of enchantments that will be able to damage and smite undead")
        @RequiresMcRestart
        public String[] holyEnchantments = new String[]{"minecraft:smite", "somanyenchantments:BlessedEdge",
                "somanyenchantments:ExtremeSmite"};

        @Name("Holy Damage")
        @Comment("A list of damage types that will be able to damage and smite undead")
        @RequiresMcRestart
        public String[] holyDamage = new String[]{"holy", "ieRevolver_silver", "aov.nimbusray"};

        @Name("Holy Material")
        @Comment("A list of material names that will be able to damage and smite undead")
        @RequiresMcRestart
        public String[] holyMaterial = new String[]{"silver"};
    }

    public static class Undying {

        @Name("Auto-Include Default Undead Mobs")
        @Comment("Set to true to include all mobs that are listed as undead by default")
        @RequiresWorldRestart
        public boolean defaultUndead = true;

        @Name("Include as Undead Mob")
        @Comment("A list of mobs that will be classified as undead by this mod in addition to the regular undead")
        @RequiresMcRestart
        public String[] undeadMobs = new String[]{};

        @Name("Smite-Proof Mobs")
        @Comment("A list of mobs that cannot lose their undying abilities")
        @RequiresMcRestart
        public String[] smiteProofMobs = new String[]{};

        @Name("Include as Unholy Mob")
        @Comment("A list of mobs that will be classified as unholy, acting like undead except they cannot be smited " +
                "by fire")
        public String[] unholyMobs = new String[]{};

        @Name("Damage Reduction")
        @Comment("Set undead natural damage reduction, in percent, against all non-holy damage")
        @RangeDouble(min = 0.0D, max = 1.0D)
        public double damageReduction = 0.8D;

        @Name("Health Regen")
        @Comment("Set undead natural health regen, in half-hearts per second")
        @RangeInt(min = 0, max = 1000)
        public int healthRegen = 1;

        @Name("Bonus Speed Modifier")
        @Comment("Set undead natural bonus speed modifier")
        @RangeDouble(min = 0.0D)
        public double speedModifier = 0.0D;

        @Name("Reduce Damage Against Non-Players")
        @Comment("Set to true to have undead reduce damage against non-player non-holy entities")
        public boolean reduceDamageVsMobs = true;
    }

    public static class ModSupport {

        @Name("Reliquary Glowing Water")
        @Comment("Set to true to give Reliquary's Glowing Water smiting")
        public boolean reliquaryGlowingWater = true;

        @Name("MK-Ultra Holy Damage")
        @Comment("List of abilities from MK-Ultra that will deal holy damage")
        public String[] mkultraSources = new String[]{"ability.smite", "ability.heal"};

        @Name("Tinkers' Holy Modifiers")
        @Comment("List of modifiers from Tinkers' Construct that will smite")
        public String[] tinkersHolyModifiers = new String[]{"smite"};
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
