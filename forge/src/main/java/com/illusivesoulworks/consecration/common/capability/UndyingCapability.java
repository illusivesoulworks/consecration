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

package com.illusivesoulworks.consecration.common.capability;

import com.illusivesoulworks.consecration.ConsecrationConstants;
import com.illusivesoulworks.consecration.api.ConsecrationApi;
import com.illusivesoulworks.consecration.api.IUndying;
import com.illusivesoulworks.consecration.common.ConsecrationEvents;
import com.illusivesoulworks.consecration.common.impl.Undying;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@SuppressWarnings("unused")
public class UndyingCapability {

  public static final Capability<IUndying> INSTANCE =
      CapabilityManager.get(new CapabilityToken<>() {
      });

  public static final ResourceLocation ID =
      new ResourceLocation(ConsecrationConstants.MOD_ID, "undying");

  @SubscribeEvent
  public void attachCapabilities(final AttachCapabilitiesEvent<Entity> evt) {

    if (evt.getObject() instanceof LivingEntity livingEntity) {
      evt.addCapability(ID, new Provider(livingEntity));
    }
  }

  @SubscribeEvent
  public void livingUpdate(final LivingEvent.LivingTickEvent evt) {
    ConsecrationApi.getInstance().getUndying(evt.getEntity()).ifPresent(IUndying::tick);
  }

  @SubscribeEvent
  public void potionAdded(final MobEffectEvent.Added evt) {
    ConsecrationApi.getInstance().getUndying(evt.getEntity())
        .ifPresent(undying -> undying.onEffectAdded(evt.getEffectInstance()));
  }

  @SubscribeEvent
  public void startTracking(final PlayerEvent.StartTracking evt) {

    if (evt.getTarget() instanceof LivingEntity livingEntity) {
      ConsecrationApi.getInstance().getUndying(livingEntity).ifPresent(IUndying::sync);
    }
  }

  @SubscribeEvent
  public void livingDamage(final LivingDamageEvent evt) {
    evt.setAmount(ConsecrationEvents.onDamaged(evt.getEntity(), evt.getSource(), evt.getAmount()));
  }

  public static class Provider implements ICapabilitySerializable<CompoundTag> {

    final LazyOptional<IUndying> optional;
    final IUndying data;

    Provider(LivingEntity livingEntity) {
      this.data = new Undying(livingEntity);
      this.optional = LazyOptional.of(() -> data);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> cap,
                                             @Nullable final Direction side) {
      return cap == UndyingCapability.INSTANCE ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
      return this.data.writeTag();
    }

    @Override
    public void deserializeNBT(final CompoundTag tag) {
      this.data.readTag(tag);
    }
  }
}
