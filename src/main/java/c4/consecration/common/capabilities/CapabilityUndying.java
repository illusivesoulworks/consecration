/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.capabilities;

import c4.consecration.Consecration;
import c4.consecration.common.UndeadHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;

public final class CapabilityUndying {

    @CapabilityInject(IUndying.class)
    public static final Capability<IUndying> UNDYING_CAP = null;

    public static final EnumFacing DEFAULT_FACING = null;
    public static final ResourceLocation ID = new ResourceLocation(Consecration.MODID, "undying");

    private static final String SMITE_TAG = "smite";

    public static void register() {
        CapabilityManager.INSTANCE.register(IUndying.class, new Capability.IStorage<IUndying>() {
            @Override
            public NBTBase writeNBT(Capability<IUndying> capability, IUndying instance, EnumFacing side) {
                NBTTagCompound compound = new NBTTagCompound();
                compound.setInteger(SMITE_TAG, instance.getSmite());
                return compound;
            }

            @Override
            public void readNBT(Capability<IUndying> capability, IUndying instance, EnumFacing side, NBTBase nbt) {
                NBTTagCompound compound = (NBTTagCompound) nbt;
                instance.setSmite(compound.getInteger(SMITE_TAG));
            }
        }, Undying::new);
    }

    @Nullable
    @SuppressWarnings("ConstantConditions")
    public static IUndying getUndying(final EntityLivingBase entityIn) {

        if (entityIn != null && entityIn.hasCapability(UNDYING_CAP, DEFAULT_FACING)) {
            return entityIn.getCapability(UNDYING_CAP, DEFAULT_FACING);
        }

        return null;
    }

    public static ICapabilityProvider createProvider(final IUndying undying) {
        return new Provider(undying, UNDYING_CAP, DEFAULT_FACING);
    }

    public static class Provider implements ICapabilitySerializable<NBTBase> {

        final Capability<IUndying> capability;
        final EnumFacing facing;
        final IUndying instance;

        Provider(final IUndying instance, final Capability<IUndying> capability, @Nullable final EnumFacing facing) {
            this.instance = instance;
            this.capability = capability;
            this.facing = facing;
        }

        @Override
        public boolean hasCapability(@Nullable final Capability<?> capability, final EnumFacing facing) {
            return capability == getCapability();
        }

        @Override
        public <T> T getCapability(@Nullable Capability<T> capability, EnumFacing facing) {
            return capability == getCapability() ? getCapability().cast(this.instance) : null;
        }

        final Capability<IUndying> getCapability() {
            return capability;
        }

        EnumFacing getFacing() {
            return facing;
        }

        final IUndying getInstance() {
            return instance;
        }

        @Override
        public NBTBase serializeNBT() {
            return getCapability().writeNBT(getInstance(), getFacing());
        }

        @Override
        public void deserializeNBT(NBTBase nbt) {
            getCapability().readNBT(getInstance(), getFacing(), nbt);
        }
    }

    @Mod.EventBusSubscriber
    private static class EventHandler {

        @SubscribeEvent
        public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> evt) {
            if (evt.getObject() instanceof EntityLivingBase && UndeadHelper.isUndead((EntityLivingBase) evt.getObject())) {
                evt.addCapability(ID, createProvider(new Undying()));
            }
        }
    }
}
