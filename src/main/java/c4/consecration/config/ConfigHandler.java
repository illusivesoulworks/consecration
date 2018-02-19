/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.config;

import c4.consecration.Consecration;
import c4.consecration.proxy.CommonProxy;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.List;

public class ConfigHandler {

    private static final String CATEGORY_GENERAL = "general";

    public static String[] undeadMobs = new String[]{};
    public static String[] unlockStage = new String[]{};
    public static int gameStageMode = 0;
    public static int dimensionMode = 0;
    public static List<Integer> dimensionsList;

    private static String[] dimensions = new String[]{};

    public static void readConfig() {
        Configuration cfg = CommonProxy.config;
        try {
            cfg.load();
            initConfig(cfg);
        } catch (Exception e) {
            Consecration.logger.log(Level.ERROR, "Problem loading config file!", e);
        } finally {
            if (cfg.hasChanged()) {
                cfg.save();
            }
        }
    }

    private static void initConfig(Configuration cfg) {
        cfg.addCustomCategoryComment(CATEGORY_GENERAL, "General configuration");
        undeadMobs = cfg.getStringList("Include as Undead Mob", CATEGORY_GENERAL, undeadMobs, "A list of mobs that will be classified as undead by this mod in addition to the regular undead");
        dimensionMode = cfg.getInt("Dimension Permission Mode", CATEGORY_GENERAL, dimensionMode, 0, 1,"Set whether the dimension configuration is blacklisted (0) or whitelisted (1)");
        dimensions = cfg.getStringList("Dimensions", CATEGORY_GENERAL, dimensions, "Set which dimensions are blacklisted or whitelisted for unkillable undead, leave blank to disable this option");
        if (Loader.isModLoaded("gamestages")) {
            gameStageMode = cfg.getInt("Game Stages Permission Mode", CATEGORY_GENERAL, gameStageMode, 0, 1, "Set whether the game stage configuration requires any (0) or all (1) of the stages listed");
            unlockStage = cfg.getStringList("Stages Required", CATEGORY_GENERAL, unlockStage, "Set which stages are required for unkillable undead, leave blank to disable this option");
        }
        initDimensionsList();
    }

    private static void initDimensionsList() {
        dimensionsList = new ArrayList<>();
        for (String s : dimensions) {
            try {
                dimensionsList.add(Integer.parseInt(s));
            } catch (Exception e) {
                Consecration.logger.log(Level.ERROR, "Problem parsing dimensions in config file!");
            }
        }
    }
}
