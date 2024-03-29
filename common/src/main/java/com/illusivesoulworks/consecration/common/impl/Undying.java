/*
 * Copyright (C) 2017-2023 Illusive Soulworks
 *
 * Consecration is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Consecration is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Consecration.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.consecration.common.impl;

import com.illusivesoulworks.consecration.api.ConsecrationApi;
import com.illusivesoulworks.consecration.api.IUndying;
import com.illusivesoulworks.consecration.api.VulnerabilityType;
import com.illusivesoulworks.consecration.common.config.ConsecrationConfig;
import com.illusivesoulworks.consecration.common.trigger.SmiteTrigger;
import com.illusivesoulworks.consecration.platform.Services;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class Undying implements IUndying {

  private static final String SMITE_TAG = "smite";
  private static final UUID SPEED_MOD = UUID.fromString("b812ef3d-0ef9-4368-845b-fad7003a1f4f");

  private final LivingEntity livingEntity;

  private int vulnerableDuration = 0;

  public Undying(LivingEntity livingEntity) {
    this.livingEntity = livingEntity;
  }

  @Override
  public void tick() {

    if (!livingEntity.level().isClientSide()) {

      if (livingEntity.tickCount % 20 == 0) {
        AttributeInstance speedAttribute = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);

        if (speedAttribute != null) {
          speedAttribute.removeModifier(SPEED_MOD);
        }

        if (this.isVulnerable()) {
          this.vulnerableDuration--;
        } else {

          if (livingEntity.getHealth() < livingEntity.getMaxHealth()) {
            livingEntity.heal((float) ConsecrationConfig.CONFIG.healthRegeneration.get());
          }
          double speedMod = (double) ConsecrationConfig.CONFIG.speedModifier.get() / 100D;

          if (speedMod > 0 && speedAttribute != null &&
              speedAttribute.getModifier(SPEED_MOD) == null) {
            speedAttribute.addTransientModifier(
                new AttributeModifier(SPEED_MOD, "Undying speed", speedMod,
                    AttributeModifier.Operation.MULTIPLY_TOTAL));
          }
        }
      }
    } else {

      if (this.isVulnerable()) {
        livingEntity.level().addParticle(ParticleTypes.INSTANT_EFFECT,
            livingEntity.position().x +
                (livingEntity.getRandom().nextDouble() - 0.5D) * (double) livingEntity.getBbWidth(),
            livingEntity.position().y +
                livingEntity.getRandom().nextDouble() * livingEntity.getBbHeight(),
            livingEntity.position().z +
                (livingEntity.getRandom().nextDouble() - 0.5D) * (double) livingEntity.getBbWidth(),
            0.0D, 0.0D, 0.0D);

        if (livingEntity.tickCount % 20 == 0) {
          this.vulnerableDuration--;
        }
      }
    }
  }

  @Override
  public boolean isVulnerable() {
    return this.vulnerableDuration > 0;
  }

  @Override
  public int getVulnerableDuration() {
    return this.vulnerableDuration;
  }

  @Override
  public void setVulnerableDuration(int duration) {
    this.vulnerableDuration = duration;
    this.sync();
  }

  @Override
  public void onEffectAdded(MobEffectInstance effectInstance) {

    if (!livingEntity.level().isClientSide()) {
      ConsecrationApi api = ConsecrationApi.getInstance();
      api.getUndying(livingEntity).ifPresent(undying -> {
        MobEffect effect = effectInstance.getEffect();

        if (api.isHolyEffect(effect)) {
          int duration =
              effect.isInstantenous() ? ConsecrationConfig.CONFIG.holyVulnerableDuration.get() :
                  effectInstance.getDuration();
          undying.setVulnerableDuration(duration);
        }
      });
    }
  }

  @Override
  public float onDamaged(DamageSource source, float damage) {

    if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
      return damage;
    }
    VulnerabilityType type =
        ConsecrationApi.getInstance().getVulnerabilityType(this.livingEntity, source);

    if (type != VulnerabilityType.NONE) {

      if (type == VulnerabilityType.FIRE) {
        this.setVulnerableDuration(ConsecrationConfig.CONFIG.fireVulnerableDuration.get());
      } else {
        this.setVulnerableDuration(ConsecrationConfig.CONFIG.holyVulnerableDuration.get());
      }

      if (source.getEntity() instanceof ServerPlayer) {
        SmiteTrigger.INSTANCE.trigger((ServerPlayer) source.getEntity());
      }
    } else if (!source.is(DamageTypeTags.BYPASSES_RESISTANCE) && !this.isVulnerable()) {
      Entity trueSource = source.getEntity();

      if (trueSource != null) {

        if (trueSource instanceof Player) {
          return damage *
              (1 - (float) ConsecrationConfig.CONFIG.damageReduction.get() / 100F);
        } else if (trueSource instanceof LivingEntity) {
          return damage *
              (1 - (float) ConsecrationConfig.CONFIG.damageReductionVsMobs.get() / 100F);
        }
      }
    }
    return damage;
  }

  @Override
  public void sync() {

    if (!livingEntity.level().isClientSide()) {
      Services.NETWORK.sendVulnerabilitySyncS2C(livingEntity, this.vulnerableDuration);
    }
  }

  @Override
  public void readTag(CompoundTag tag) {
    this.setVulnerableDuration(tag.getInt(SMITE_TAG));
  }

  @Override
  public LivingEntity getLivingEntity() {
    return this.livingEntity;
  }

  @Override
  public CompoundTag writeTag() {
    CompoundTag tag = new CompoundTag();
    tag.putInt(SMITE_TAG, this.getVulnerableDuration());
    return tag;
  }
}
