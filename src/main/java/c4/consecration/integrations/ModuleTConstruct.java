/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.integrations;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Loader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.BlockSoil;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.TinkerTraits;

public class ModuleTConstruct extends ModuleCompatibility {

    public ModuleTConstruct() {
        super("tconstruct");
    }

    @Override
    public boolean process(EntityLivingBase target, DamageSource source) {

        if (TConstruct.pulseManager.isPulseLoaded(TinkerTools.PulseId)) {

            if (source.getImmediateSource() instanceof EntityLivingBase) {

                ItemStack stack = ((EntityLivingBase) source.getImmediateSource()).getHeldItemMainhand();

                if (TConstruct.pulseManager.isPulseLoaded(TinkerTools.PulseId) && stack.getItem() instanceof TinkersItem) {
                    if (TinkerUtil.hasTrait(TagUtil.getTagSafe(stack), TinkerTraits.holy.getIdentifier()) || TinkerUtil.hasModifier(TagUtil.getTagSafe(stack), TinkerModifiers.modSmite.getIdentifier())) {
                        return true;
                    }
                }

            } else {

                if (source.getImmediateSource() instanceof EntityProjectileBase) {
                    if (TConstruct.pulseManager.isPulseLoaded(TinkerTools.PulseId)) {
                        ItemStack stack = ((EntityProjectileBase) source.getImmediateSource()).tinkerProjectile.getItemStack();
                        if (TinkerUtil.hasTrait(TagUtil.getTagSafe(stack), TinkerTraits.holy.getIdentifier()) || TinkerUtil.hasModifier(TagUtil.getTagSafe(stack), TinkerModifiers.modSmite.getIdentifier())) {
                            return true;
                        }
                    }
                }
            }
        }

        BlockPos standingOn = target.getPosition().down();
        IBlockState state = target.world.getBlockState(standingOn);
        if (state.getBlock() == TinkerCommons.blockSoil && state.getValue(BlockSoil.TYPE) == BlockSoil.SoilTypes.CONSECRATED) {
            return true;
        }

        return false;
    }
}
