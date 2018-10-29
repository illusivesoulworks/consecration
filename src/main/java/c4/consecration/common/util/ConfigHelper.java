/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.util;

import c4.consecration.common.capabilities.CapabilityUndying;
import c4.consecration.common.config.ConfigHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ConfigHelper {

    public static void parseDimensionConfigs() {
        if (ConfigHandler.dimensionList.length > 0) {
            for (String s : ConfigHandler.dimensionList) {
                int dimension = Integer.parseInt(s);
                CapabilityUndying.EventHandler.addDimension(dimension);
            }
        }
    }

    public static void registerConfigs() {
        ConfigHandler.Holy holy = ConfigHandler.holy;
        for (String s : holy.holyEntities) {
            UndeadRegistry.addHolyEntity(new ResourceLocation(s));
        }
        for (String s : holy.holyDamage) {
            UndeadRegistry.addHolyDamage(s);
        }
        for (String s : holy.holyEnchantments) {
            UndeadRegistry.addHolyEnchantment(new ResourceLocation(s));
        }
        for (String s : holy.holyMaterial) {
            UndeadRegistry.addHolyMaterial(s);
        }
        for (String s : holy.holyPotions) {
            UndeadRegistry.addHolyPotion(new ResourceLocation(s));
        }
        for (String s : holy.holyWeapons) {
            UndeadRegistry.addHolyWeapon(s);
        }
        for (String s : ConfigHandler.undying.undeadMobs) {
            UndeadRegistry.addUndead(new ResourceLocation(s));
        }
        for (String s : ConfigHandler.undying.unholyMobs) {
            UndeadRegistry.addUnholy(new ResourceLocation(s));
        }
    }
}
