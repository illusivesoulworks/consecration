/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.integrations;

import c4.consecration.common.util.UndeadRegistry;
import net.minecraft.util.ResourceLocation;

public class ModuleSME extends ModuleCompatibility {

    public ModuleSME() {
        super("somanyenchantments");
    }

    @Override
    public void register() {
        UndeadRegistry.addHolyEnchantment(new ResourceLocation(modid, "BlessedEdge"));
        UndeadRegistry.addHolyEnchantment(new ResourceLocation(modid, "ExtremeSmite"));
    }
}
