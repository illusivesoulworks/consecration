/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.integrations;

import c4.consecration.common.util.UndeadHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.BlockSoil;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.TinkerTraits;

import java.util.List;

public class ModuleTConstruct extends ModuleCompatibility {

    public ModuleTConstruct() {
        super("tconstruct");
    }

    @Override
    public boolean process(EntityLivingBase target, DamageSource source) {

        ItemStack stack;

        if (source.getImmediateSource() instanceof EntityProjectileBase) {
            stack = ((EntityProjectileBase) source.getImmediateSource()).tinkerProjectile.getItemStack();

            if (hasHolyMaterial(TagUtil.getBaseMaterialsTagList(stack))) {
                return true;
            }
        }

        if (source.getTrueSource() instanceof EntityLivingBase) {
            stack = ((EntityLivingBase) source.getTrueSource()).getHeldItemMainhand();

            if (hasHolyMaterial(TagUtil.getBaseMaterialsTagList(stack))) {
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

    private boolean hasHolyMaterial(NBTTagList materialTag) {

        for (int i = 0; i < materialTag.tagCount(); i++) {

            if (UndeadHelper.isHolyMaterial(materialTag.getStringTagAt(i))) {
                return true;
            }
        }
        return false;
    }
}
