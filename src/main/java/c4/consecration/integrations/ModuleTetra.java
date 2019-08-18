/*
 * Copyright (c) 2019 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.integrations;

import c4.consecration.common.util.UndeadHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import se.mickelus.tetra.items.IItemModular;

public class ModuleTetra extends ModuleCompatibility {

    public ModuleTetra() {
        super("tetra");
    }

    @Override
    public boolean process(EntityLivingBase target, DamageSource source) {

        if (source.getImmediateSource() instanceof EntityLivingBase) {
            ItemStack stack = ((EntityLivingBase) source.getImmediateSource()).getHeldItemMainhand();

            if (stack.getItem() instanceof IItemModular && stack.hasTagCompound()) {
                NBTTagCompound compound = stack.getTagCompound();

                for (String key : compound.getKeySet()) {

                    if (key.contains("_material")) {
                        String value = compound.getString(key);

                        if (UndeadHelper.isHolyMaterial(value)) {
                            return true;
                        }
                    } else if (key.contains("enchantment/smite")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
