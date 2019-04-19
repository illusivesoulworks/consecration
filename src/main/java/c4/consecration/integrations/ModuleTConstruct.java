/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.integrations;

import c4.consecration.common.config.ConfigHandler;
import c4.consecration.common.util.UndeadHelper;
import c4.consecration.common.util.UndeadRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.BlockSoil;

public class ModuleTConstruct extends ModuleCompatibility {

    public ModuleTConstruct() {
        super("tconstruct");
    }

    @Override
    public boolean process(EntityLivingBase target, DamageSource source) {

        ItemStack stack = ItemStack.EMPTY;
        NBTTagList materials;
        NBTTagList modifiers;

        if (source.getImmediateSource() instanceof EntityProjectileBase) {
            stack = ((EntityProjectileBase) source.getImmediateSource()).tinkerProjectile.getItemStack();
        } else if (source.getTrueSource() instanceof EntityLivingBase) {
            stack = ((EntityLivingBase) source.getTrueSource()).getHeldItemMainhand();
        }

        if (!stack.isEmpty()) {
            materials = TagUtil.getBaseMaterialsTagList(stack);
            modifiers = TagUtil.getModifiersTagList(stack);

            if (hasHolyMaterial(materials, modifiers)) {
                return true;
            }
        }

        BlockPos standingOn = target.getPosition().down();
        IBlockState state = target.world.getBlockState(standingOn);
        if (state.getBlock() == TinkerCommons.blockSoil && state.getValue(BlockSoil.TYPE) == BlockSoil.SoilTypes.CONSECRATED) {
            return true;
        }
        return false;
    }

    private boolean hasHolyMaterial(NBTTagList materialTag, NBTTagList modifierTag) {

        for (int i = 0; i < materialTag.tagCount(); i++) {

            if (UndeadHelper.isHolyMaterial(materialTag.getStringTagAt(i))) {
                return true;
            }
        }

        for (int j = 0; j < modifierTag.tagCount(); j++) {
            String name = modifierTag.getCompoundTagAt(j).getString("identifier");

            for (String materials : UndeadRegistry.getHolyMaterials()) {
                String pattern = "extratrait" + materials + "(.*|\\b)";

                if (name.matches(pattern)) {
                    return true;
                }
            }

            for (String s : ConfigHandler.modSupport.tinkersHolyModifiers) {

                if (name.equals(s)) {
                    return true;
                }
            }
        }
        return false;
    }
}
