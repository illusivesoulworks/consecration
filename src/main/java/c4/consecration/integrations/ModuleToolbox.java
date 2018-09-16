/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.integrations;

import api.materials.PartMaterial;
import c4.consecration.common.UndeadHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.Optional;
import toolbox.common.items.tools.*;

import java.util.ArrayList;
import java.util.List;

public class ModuleToolbox extends ModuleCompatibility {

    public ModuleToolbox() {
        super("toolbox");
    }

    @Override
    public boolean process(EntityLivingBase entity, DamageSource source) {

        if (source.getImmediateSource() == null || !(source.getImmediateSource() instanceof EntityLivingBase)) {
            return false;
        }

        ItemStack stack = ((EntityLivingBase) source.getImmediateSource()).getHeldItemMainhand();

        return isHolyToolbox(stack);
    }

    @Optional.Method(modid = "toolbox")
    private boolean isHolyToolbox(ItemStack stack) {
        Item item = stack.getItem();
        List<PartMaterial> materials = new ArrayList<>();
        if (item instanceof IHeadTool) {
            materials.add(IHeadTool.getHeadMat(stack));
        }
        if (item instanceof IBladeTool) {
            materials.add(IBladeTool.getBladeMat(stack));
        }
        if (item instanceof ICrossguardTool) {
            materials.add(ICrossguardTool.getCrossguardMat(stack));
        }
        if (item instanceof IHaftTool) {
            materials.add(IHaftTool.getHaftMat(stack));
        }
        if (item instanceof IHandleTool) {
            materials.add(IHandleTool.getHandleMat(stack));
        }
        if (item instanceof IAdornedTool) {
            materials.add(IAdornedTool.getAdornmentMat(stack));
        }

        for (PartMaterial mat : materials) {
            if (UndeadHelper.isHolyMaterial(mat.getName())) {
                return true;
            }
        }

        return false;
    }
}
