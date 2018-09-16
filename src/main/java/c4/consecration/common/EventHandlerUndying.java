/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common;

import c4.consecration.common.capabilities.CapabilityUndying;
import c4.consecration.common.capabilities.IUndying;
import c4.consecration.config.HandlerConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;
import java.util.Set;

public class EventHandlerUndying {

    public static Set<Integer> dimensions = new HashSet<>();

    private static final int SMITE_DURATION = 400;

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent evt) {
        EntityLivingBase entitylivingbase = evt.getEntityLiving();

        if (!entitylivingbase.getEntityWorld().isRemote) {
            IUndying undying = CapabilityUndying.getUndying(entitylivingbase);

            if (undying != null) {

                if (UndeadHelper.isSmote(entitylivingbase, undying)) {

                    if (entitylivingbase.ticksExisted % 10 == 0) {
                        WorldServer worldIn = (WorldServer) entitylivingbase.getEntityWorld();
                        worldIn.spawnParticle(EnumParticleTypes.SPELL_INSTANT, entitylivingbase.posX,
                                entitylivingbase.posY + entitylivingbase.height / 2.0D, entitylivingbase.posZ, 2,
                                entitylivingbase.width / 2.0D, entitylivingbase.height / 4.0D,
                                entitylivingbase.width / 2.0D, 0.0D);
                    }
                    undying.decrementSmite();

                } else if (entitylivingbase.ticksExisted % 20 == 0) {
                    entitylivingbase.heal(HandlerConfig.undying.healthRegen);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent evt) {
        EntityLivingBase entitylivingbase = evt.getEntityLiving();

        if (!entitylivingbase.getEntityWorld().isRemote) {
            DamageSource source = evt.getSource();
            IUndying undying = CapabilityUndying.getUndying(entitylivingbase);

            if (undying != null) {
                if (UndeadHelper.doSmite(entitylivingbase, source)) {
                    undying.setSmite(SMITE_DURATION);
                } else if (source != DamageSource.OUT_OF_WORLD && !source.isDamageAbsolute()
                        && !UndeadHelper.isSmote(entitylivingbase, undying)) {
                    evt.setAmount(evt.getAmount() * (float) (1 - HandlerConfig.undying.damageReduction));
                }
            }
        }
    }

    public boolean isAllowedDimension(EntityLivingBase entity) {
        int dimension = entity.dimension;
        if (dimensions.isEmpty()) {
            return true;
        } else if (HandlerConfig.dimensionPermission == HandlerConfig.PermissionMode.BLACKLIST) {
            return !dimensions.contains(dimension);
        } else {
            return dimensions.contains(dimension);
        }
    }
}
