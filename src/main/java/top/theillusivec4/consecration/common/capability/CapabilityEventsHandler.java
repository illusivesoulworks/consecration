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

import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionAddedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.consecration.api.ConsecrationApi;
import top.theillusivec4.consecration.api.IUndying;
import top.theillusivec4.consecration.api.VulnerabilityType;
import top.theillusivec4.consecration.common.ConsecrationConfig;
import top.theillusivec4.consecration.common.network.ConsecrationNetwork;
import top.theillusivec4.consecration.common.trigger.SmiteTrigger;

@SuppressWarnings("unused")
public class CapabilityEventsHandler {

  private static final UUID SPEED_MOD = UUID.fromString("b812ef3d-0ef9-4368-845b-fad7003a1f4f");

  @SubscribeEvent
  public void attachCapabilities(final AttachCapabilitiesEvent<Entity> evt) {

    if (evt.getObject() instanceof LivingEntity livingEntity) {
      evt.addCapability(UndyingCapability.ID, new Provider(livingEntity));
    }
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void invalidateCaps(final EntityLeaveWorldEvent evt) {
    Entity entity = evt.getEntity();

    if (entity instanceof LivingEntity livingEntity) {
      LazyOptional<IUndying> cap = UndyingCapability.get(livingEntity);

      if (cap.isPresent()) {
        cap.invalidate();
      }
    }
  }

  @SubscribeEvent
  public void livingUpdate(final LivingEvent.LivingUpdateEvent evt) {
    LivingEntity livingEntity = evt.getEntityLiving();

    if (!livingEntity.getLevel().isClientSide()) {

      if (livingEntity.tickCount % 20 == 0) {
        ConsecrationApi.getInstance().getUndying(livingEntity).ifPresent(undying -> {
          AttributeInstance speedAttribute = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);

          if (speedAttribute != null) {
            speedAttribute.removeModifier(SPEED_MOD);
          }

          if (undying.isVulnerable()) {
            undying.decrementVulnerability();
          } else {

            if (livingEntity.getHealth() < livingEntity.getMaxHealth()) {
              livingEntity.heal((float) ConsecrationConfig.healthRegeneration);
            }
            double speedMod = ConsecrationConfig.speedModifier;

            if (speedMod > 0 && speedAttribute != null &&
                speedAttribute.getModifier(SPEED_MOD) == null) {
              speedAttribute.addTransientModifier(
                  new AttributeModifier(SPEED_MOD, "Undead speed", speedMod,
                      Operation.MULTIPLY_TOTAL));
            }
          }
        });
      }
    } else {
      ConsecrationApi.getInstance().getUndying(livingEntity).ifPresent(undying -> {

        if (undying.isVulnerable()) {
          livingEntity.getLevel().addParticle(ParticleTypes.INSTANT_EFFECT,
              livingEntity.position().x + (livingEntity.getRandom().nextDouble() - 0.5D) *
                  (double) livingEntity.getBbWidth(), livingEntity.position().y +
                  livingEntity.getRandom().nextDouble() * livingEntity.getBbHeight(),
              livingEntity.position().z + (livingEntity.getRandom().nextDouble() - 0.5D) *
                  (double) livingEntity.getBbWidth(), 0.0D, 0.0D, 0.0D);
        }

        if (livingEntity.tickCount % 20 == 0) {
          undying.decrementVulnerability();
        }
      });
    }
  }

  @SubscribeEvent
  public void potionAdded(final PotionAddedEvent evt) {
    LivingEntity livingEntity = evt.getEntityLiving();

    if (!livingEntity.getLevel().isClientSide()) {
      ConsecrationApi.getInstance().getUndying(livingEntity).ifPresent(undying -> {
        MobEffectInstance effectInstance = evt.getPotionEffect();
        MobEffect effect = effectInstance.getEffect();

        if (ConsecrationApi.getInstance().isHolyEffect(effect)) {
          int duration =
              effect.isInstantenous() ? ConsecrationConfig.holyVulnerableDuration :
                  effectInstance.getDuration();
          undying.setVulnerableDuration(duration);
        }
      });
    }
  }

  @SubscribeEvent
  public void startTracking(final PlayerEvent.StartTracking evt) {

    if (evt.getTarget() instanceof LivingEntity livingEntity &&
        !livingEntity.getLevel().isClientSide()) {
      ConsecrationApi.getInstance().getUndying(livingEntity).ifPresent(
          undying -> ConsecrationNetwork.syncVulnerability(livingEntity,
              undying.getVulnerableDuration()));
    }
  }

  @SubscribeEvent
  public void livingDamage(final LivingDamageEvent evt) {
    LivingEntity livingEntity = evt.getEntityLiving();

    if (!livingEntity.getLevel().isClientSide()) {
      LazyOptional<IUndying> hurtUndying = ConsecrationApi.getInstance().getUndying(livingEntity);

      if (!hurtUndying.isPresent() &&
          evt.getSource().getDirectEntity() instanceof LivingEntity attacker) {
        LazyOptional<IUndying> attackerUndying = ConsecrationApi.getInstance().getUndying(attacker);

        attackerUndying.ifPresent(undying -> {
          int level =
              ConsecrationApi.getInstance()
                  .getHolyProtectionLevel(attacker, livingEntity, evt.getSource());

          if (level > 0 &&
              livingEntity.getLevel().getRandom().nextFloat() < 0.15F * (float) level) {
            undying.setVulnerableDuration(ConsecrationConfig.holyVulnerableDuration);
          }
        });
      }

      hurtUndying.ifPresent(undying -> {
        DamageSource source = evt.getSource();

        if (source == DamageSource.OUT_OF_WORLD || source == DamageSource.CRAMMING
            || source == DamageSource.IN_WALL) {
          return;
        }
        VulnerabilityType type = UndyingCapability.createVulnerability(livingEntity, source);

        if (type != VulnerabilityType.NONE) {

          if (type == VulnerabilityType.FIRE) {
            undying.setVulnerableDuration(ConsecrationConfig.fireVulnerableDuration);
          } else {
            undying.setVulnerableDuration(ConsecrationConfig.holyVulnerableDuration);
          }

          if (source.getEntity() instanceof ServerPlayer) {
            SmiteTrigger.INSTANCE.trigger((ServerPlayer) source.getEntity());
          }
        } else if (!source.isBypassMagic() && !undying.isVulnerable()) {
          Entity trueSource = source.getEntity();

          if (trueSource != null) {

            if (trueSource instanceof Player) {
              evt.setAmount(evt.getAmount() * (float) (1 - ConsecrationConfig.damageReduction));
            } else if (trueSource instanceof LivingEntity) {
              evt.setAmount(
                  evt.getAmount() * (float) (1 - ConsecrationConfig.damageReductionVsMobs));
            }
          }
        }
      });
    }
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
