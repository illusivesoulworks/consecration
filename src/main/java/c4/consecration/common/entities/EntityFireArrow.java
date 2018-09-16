/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.entities;

import c4.consecration.init.HolderConsecration;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
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
    protected void onHit(RayTraceResult raytraceResultIn) {
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
    @SuppressWarnings("ConstantConditions")
    protected ItemStack getArrowStack() {
        return new ItemStack(HolderConsecration.fireArrow);
    }
}
