/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.integrations;

import c4.consecration.common.capabilities.CapabilityUndying;
import c4.consecration.common.capabilities.IUndying;
import c4.consecration.common.config.ConfigHandler;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class ModuleReliquary extends ModuleCompatibility {

    public ModuleReliquary() {
        super("xreliquary", true);
    }

    @SubscribeEvent
    public void onThrowable(ProjectileImpactEvent.Throwable evt) {

        if (ConfigHandler.modSupport.reliquaryGlowingWater && !evt.getThrowable().world.isRemote) {

            EntityThrowable entityThrowable = evt.getThrowable();
            ResourceLocation rl = EntityList.getKey(entityThrowable);

            if (rl != null && rl.toString().equals("xreliquary:holy_water")) {

                AxisAlignedBB bb = entityThrowable.getEntityBoundingBox().grow(4.0D, 2.0D, 4.0D);
                List<EntityLivingBase> eList = entityThrowable.world.getEntitiesWithinAABB(EntityLivingBase.class, bb);
                for (EntityLivingBase entity : eList) {
                    IUndying undying = CapabilityUndying.getUndying(entity);
                    if (undying != null) {
                        undying.setSmite(200);
                    }
                }
            }
        }
    }
}
