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

import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionAddedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.consecration.api.ConsecrationAPI;
import top.theillusivec4.consecration.common.ConsecrationConfig;
import top.theillusivec4.consecration.common.ConsecrationUtils;
import top.theillusivec4.consecration.common.ConsecrationUtils.DamageType;
import top.theillusivec4.consecration.common.capability.UndyingCapability.IUndying;
import top.theillusivec4.consecration.common.trigger.SmiteTrigger;

public class CapabilityEventsHandler {

  private static final UUID SPEED_MOD = UUID.fromString("b812ef3d-0ef9-4368-845b-fad7003a1f4f");

  @SubscribeEvent
  public void attachCapabilities(final AttachCapabilitiesEvent<Entity> evt) {

    if (evt.getObject() instanceof LivingEntity && ConsecrationUtils
        .isUndying((LivingEntity) evt.getObject())) {
      evt.addCapability(UndyingCapability.ID, new UndyingCapability.Provider());
    }
  }

  @SubscribeEvent
  public void onLivingUpdate(LivingEvent.LivingUpdateEvent evt) {
    LivingEntity livingEntity = evt.getEntityLiving();

    if (!livingEntity.getEntityWorld().isRemote) {
      LazyOptional<IUndying> undyingOpt = UndyingCapability.getCapability(livingEntity);

      undyingOpt.ifPresent(undying -> {
        IAttributeInstance speedAttribute = livingEntity
            .getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        speedAttribute.removeModifier(SPEED_MOD);

        if (undying.hasSmite()) {

          if (livingEntity.ticksExisted % 10 == 0) {
            ServerWorld world = (ServerWorld) livingEntity.getEntityWorld();
            world.spawnParticle(ParticleTypes.INSTANT_EFFECT, livingEntity.getPosX(),
                livingEntity.getPosY() + livingEntity.getHeight() / 2.0D, livingEntity.getPosZ(), 2,
                livingEntity.getWidth() / 2.0D, livingEntity.getHeight() / 4.0D,
                livingEntity.getWidth() / 2.0D, 0.0D);
          }
          undying.tickSmite();
        } else {

          if (livingEntity.ticksExisted % 20 == 0 && livingEntity.getHealth() < livingEntity
              .getMaxHealth()) {
            livingEntity.heal(ConsecrationConfig.SERVER.healthRegen.get());
          }
          double speedMod = ConsecrationConfig.SERVER.speedModifier.get();

          if (speedMod > 0 && speedAttribute.getModifier(SPEED_MOD) == null) {
            speedAttribute.applyModifier(new AttributeModifier(SPEED_MOD, "Undead speed", speedMod,
                Operation.MULTIPLY_TOTAL));
          }
        }
      });
    }
  }

  @SubscribeEvent
  public void onPotionAdded(PotionAddedEvent evt) {
    LivingEntity livingEntity = evt.getEntityLiving();

    if (!livingEntity.getEntityWorld().isRemote) {
      LazyOptional<IUndying> undyingOpt = UndyingCapability.getCapability(livingEntity);

      undyingOpt.ifPresent(undying -> {
        EffectInstance effectInstance = evt.getPotionEffect();
        Set<Effect> effect = ConsecrationAPI.getHolyEffects();
        Effect effect1 = effectInstance.getPotion();
        if (effect.contains(effect1)) {
          int duration = effect1.isInstant() ? ConsecrationConfig.SERVER.holySmiteDuration.get()
              : effectInstance.getDuration();
          undying.setSmiteDuration(duration * 20);
        }
      });
    }
  }

  @SubscribeEvent
  public void onLivingDamage(LivingDamageEvent evt) {
    LivingEntity livingEntity = evt.getEntityLiving();

    if (!livingEntity.getEntityWorld().isRemote) {
      LazyOptional<IUndying> undyingOpt = UndyingCapability.getCapability(livingEntity);

      if (!undyingOpt.isPresent() && evt.getSource().getImmediateSource() instanceof LivingEntity) {
        LivingEntity attacker = (LivingEntity) evt.getSource().getImmediateSource();
        LazyOptional<IUndying> undyingOpt2 = UndyingCapability.getCapability(attacker);

        undyingOpt2.ifPresent(undying -> {
          int level = ConsecrationUtils.protect(attacker, livingEntity, evt.getSource());

          if (level > 0 && livingEntity.getEntityWorld().rand.nextFloat() < 0.15F * (float) level) {
            undying.setSmiteDuration(ConsecrationConfig.SERVER.holySmiteDuration.get() * 20);
          }
        });
      }

      undyingOpt.ifPresent(undying -> {
        DamageSource source = evt.getSource();

        if (source == DamageSource.OUT_OF_WORLD || source == DamageSource.CRAMMING
            || source == DamageSource.IN_WALL) {
          return;
        }
        DamageType type = ConsecrationUtils.smite(livingEntity, source);

        if (type != DamageType.NONE) {

          if (type == DamageType.FIRE) {
            undying.setSmiteDuration(ConsecrationConfig.SERVER.fireSmiteDuration.get() * 20);
          } else {
            undying.setSmiteDuration(ConsecrationConfig.SERVER.holySmiteDuration.get() * 20);
          }

          if (source.getTrueSource() instanceof ServerPlayerEntity) {
            SmiteTrigger.INSTANCE.trigger((ServerPlayerEntity) source.getTrueSource());
          }
        } else if (!source.isDamageAbsolute() && !undying.hasSmite()) {
          Entity trueSource = source.getTrueSource();

          if ((trueSource != null && (trueSource instanceof PlayerEntity
              || ConsecrationConfig.SERVER.bystanderNerf.get()))) {
            evt.setAmount(
                evt.getAmount() * (float) (1 - ConsecrationConfig.SERVER.damageReduction.get()));
          }
        }
      });
    }
  }
}
