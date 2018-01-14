/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.blocks;

import c4.consecration.Consecration;
import c4.consecration.init.ModPotions;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockHallowedGrounds extends Block {

    private Random rand = new Random();

    public BlockHallowedGrounds() {
        super(Material.GROUND);
        this.setRegistryName("hallowed_grounds");
        this.setUnlocalizedName(Consecration.MODID + ".hallowed_grounds");
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        this.setLightLevel(0.2F);
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
    {
        double expandY = 0.0D;

        if (entityIn instanceof EntityLivingBase && ((EntityLivingBase) entityIn).isEntityUndead()) {
            EntityLivingBase undead = (EntityLivingBase) entityIn;
            if (!undead.isAirBorne) {
                expandY = 1.0D;
            }
        }

        addCollisionBoxToList(pos, entityBox, collidingBoxes, state.getCollisionBoundingBox(worldIn, pos).expand(0.0D, expandY, 0.0D));
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {

        this.dealHolyDamage(worldIn, pos, entityIn);
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        this.dealHolyDamage(worldIn, pos, entityIn);
    }

    private void dealHolyDamage(World worldIn, BlockPos pos, Entity entityIn) {

        if (entityIn instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase) entityIn;
            if (entityLivingBase.isEntityUndead()) {
                if (entityLivingBase.attackEntityFrom(ModPotions.HOLY_DAMAGE, Math.min(20, entityLivingBase.getMaxHealth())) && rand.nextInt(4) == 0) {
                    worldIn.setBlockState(pos, Blocks.DIRT.getDefaultState());
                }
                entityLivingBase.setFire(4);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
