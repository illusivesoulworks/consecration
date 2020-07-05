/*
 * Copyright (c) 2017-2020 C4
 *
 * This file is part of Consecration, a mod made for Minecraft.
 *
 * Consecration is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Consecration is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Consecration.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.consecration.common.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.consecration.Consecration;

public class UndyingCapability {

  @CapabilityInject(IUndying.class)
  public static final Capability<IUndying> UNDYING_CAP;

  public static final ResourceLocation ID = new ResourceLocation(Consecration.MODID, "undying");

  private static final String SMITE_TAG = "smite";

  static {
    UNDYING_CAP = null;
  }

  public static void register() {
    CapabilityManager.INSTANCE.register(IUndying.class, new IStorage<IUndying>() {

      @Override
      public INBT writeNBT(Capability<IUndying> capability, IUndying instance, Direction side) {
        CompoundNBT compound = new CompoundNBT();
        compound.putInt(SMITE_TAG, instance.getSmiteDuration());
        return compound;
      }

      @Override
      public void readNBT(Capability<IUndying> capability, IUndying instance, Direction side,
          INBT nbt) {
        CompoundNBT compound = (CompoundNBT) nbt;
        instance.setSmiteDuration(compound.getInt(SMITE_TAG));
      }
    }, Undying::new);
    MinecraftForge.EVENT_BUS.register(new CapabilityEventsHandler());
  }

  public static LazyOptional<IUndying> getCapability(final LivingEntity entity) {
    return entity.getCapability(UNDYING_CAP);
  }

  public interface IUndying {

    boolean hasSmite();

    int getSmiteDuration();

    void setSmiteDuration(int duration);

    void tickSmite();
  }

  public static class Undying implements IUndying {

    private int smiteDuration = 0;

    @Override
    public boolean hasSmite() {
      return smiteDuration > 0;
    }

    @Override
    public int getSmiteDuration() {
      return smiteDuration;
    }

    @Override
    public void setSmiteDuration(int duration) {
      this.smiteDuration = duration;
    }

    @Override
    public void tickSmite() {

      if (this.smiteDuration > 0) {
        this.smiteDuration--;
      }
    }
  }

  public static class Provider implements ICapabilitySerializable<INBT> {

    final LazyOptional<IUndying> optional;
    final IUndying data;

    Provider() {
      this.data = new Undying();
      this.optional = LazyOptional.of(() -> data);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nullable Capability<T> capability, Direction side) {
      return UNDYING_CAP.orEmpty(capability, optional);
    }

    @Override
    public INBT serializeNBT() {
      return UNDYING_CAP.writeNBT(data, null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
      UNDYING_CAP.readNBT(data, null, nbt);
    }
  }
}
