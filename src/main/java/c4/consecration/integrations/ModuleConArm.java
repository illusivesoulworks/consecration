/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.integrations;

import c4.conarm.lib.tinkering.TinkersArmor;
import c4.consecration.common.util.UndeadHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import slimeknights.tconstruct.library.utils.TagUtil;

public class ModuleConArm extends ModuleCompatibility {

    public ModuleConArm() {
        super("conarm");
    }

    @Override
    public boolean processArmor(EntityLivingBase wearer, ItemStack stack, DamageSource source) {
        return stack.getItem() instanceof TinkersArmor && hasHolyMaterial(TagUtil.getBaseMaterialsTagList(stack));
    }

    private boolean hasHolyMaterial(NBTTagList materialTag) {

        for (int i = 0; i < materialTag.tagCount(); i++) {

            if (UndeadHelper.isHolyMaterial(materialTag.getStringTagAt(i))) {
                return true;
            }
        }
        return false;
    }
}
