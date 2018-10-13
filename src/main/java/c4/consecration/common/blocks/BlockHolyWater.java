/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.blocks;

import c4.consecration.Consecration;
import c4.consecration.common.init.ConsecrationDamageSources;
import c4.consecration.common.init.ConsecrationFluids;
import c4.consecration.common.items.ItemBlessedDust;
import c4.consecration.common.util.UndeadHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockHolyWater extends BlockFluidClassic {

    public BlockHolyWater() {
        super(ConsecrationFluids.HOLY_WATER, Material.WATER);
        this.setRegistryName("holy_water");
        this.setTranslationKey(Consecration.MODID + ".holy_water");
        this.setCreativeTab(CreativeTabs.MATERIALS);
    }

    @Nullable
    @Override
    public PathNodeType getAiPathNodeType(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        return PathNodeType.LAVA;
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity)
    {
        if (entity instanceof EntityLivingBase) {

            if (UndeadHelper.isUndead((EntityLivingBase)entity)) {
                entity.attackEntityFrom(ConsecrationDamageSources.HOLY, 1.0f);
            } else {
                ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100));
            }
        }
    }
}
