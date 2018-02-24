/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.entities;

import c4.consecration.init.ModItems;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class EntityFireArrow extends EntityArrow {

    public EntityFireArrow(World worldIn)
    {
        super(worldIn);
    }

    public EntityFireArrow(World worldIn, double x, double y, double z)
    {
        super(worldIn, x, y, z);
    }

    public EntityFireArrow(World worldIn, EntityLivingBase shooter)
    {
        super(worldIn, shooter);
    }

    @Override
    public void onUpdate() {

        super.onUpdate();

        this.setFire(100);

    }

    @Override
    protected void onHit(RayTraceResult raytraceResultIn)
    {
        super.onHit(raytraceResultIn);

        if (!world.isRemote) {

            Entity entity = raytraceResultIn.entityHit;

            if (entity != null) {
                if (entity.isEntityAlive() && !entity.isImmuneToFire()) {
                    entity.setFire(4);
                }
            }
        }
    }

    @Override
    @Nonnull
    protected ItemStack getArrowStack() {
        return new ItemStack(ModItems.fireArrow);
    }
}
