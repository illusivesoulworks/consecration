/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.entities;

import c4.consecration.client.RenderFireArrow;
import c4.consecration.client.RenderFireBomb;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class EntityFireBomb extends EntityThrowable {

    public EntityFireBomb(World worldIn)
    {
        super(worldIn);
    }

    public EntityFireBomb(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntityFireBomb(World worldIn, EntityLivingBase throwerIn)
    {
        super(worldIn, throwerIn);
    }

    @Override
    protected float getGravityVelocity()
    {
        return 0.05F;
    }

    @Override
    protected void onImpact(RayTraceResult result)
    {
        if (!this.world.isRemote)
        {
            AxisAlignedBB bb = this.getEntityBoundingBox().grow(2.0D, 2.0D, 2.0D);
            List<EntityLivingBase> eList = this.world.getEntitiesWithinAABB(EntityLivingBase.class, bb);
            for (EntityLivingBase entity : eList) {
                entity.setFire(8);
            }

            this.world.playEvent(2007, new BlockPos(this), 0);
            this.world.newExplosion(this, this.posX, this.posY, this.posZ, 1.0F, true, false);
            this.setDead();
        }
    }

    @SideOnly(Side.CLIENT)
    public static void initModel() {
        RenderingRegistry.registerEntityRenderingHandler(EntityFireBomb.class, RenderFireBomb.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityFireArrow.class, RenderFireArrow.FACTORY);
    }
}
