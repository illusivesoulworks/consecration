/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.integrations;

import c4.consecration.common.util.UndeadRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ModuleDCS extends ModuleCompatibility {

    public ModuleDCS() {
        super("dcs_climate");
    }

    @Override
    public void register() {

        UndeadRegistry.addHolyEntity(new ResourceLocation(modid, "dcs.main.bullet_silver"));
        Item item = Item.getByNameOrId("dcs_climate:dcs_dagger_silver");
        if (item != null) {
            UndeadRegistry.addHolyWeapon(new ItemStack(item));
        }
    }
}
