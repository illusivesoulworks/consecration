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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionAddedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.consecration.api.ConsecrationApi;
import top.theillusivec4.consecration.common.ConsecrationConfig;
import top.theillusivec4.consecration.common.ConsecrationUtils;
import top.theillusivec4.consecration.common.ConsecrationUtils.DamageType;
import top.theillusivec4.consecration.common.capability.UndyingCapability.IUndying;
import top.theillusivec4.consecration.common.capability.UndyingCapability.Provider;
import top.theillusivec4.consecration.common.trigger.SmiteTrigger;

public class CapabilityEventsHandler {

  private static final UUID SPEED_MOD = UUID.fromString("b812ef3d-0ef9-4368-845b-fad7003a1f4f");

  @SubscribeEvent
  public void attachCapabilities(final AttachCapabilitiesEvent<Entity> evt) {

    if (evt.getObject() instanceof LivingEntity) {
      evt.addCapability(UndyingCapability.ID, new Provider());
    }
  }

  @SubscribeEvent
  public void onLivingUpdate(LivingEvent.LivingUpdateEvent evt) {
    LivingEntity livingEntity = evt.getEntityLiving();

    if (!livingEntity.getCommandSenderWorld().isClientSide && ConsecrationUtils.isUndying(livingEntity)) {
      LazyOptional<IUndying> undyingOpt = UndyingCapability.getCapability(livingEntity);

      undyingOpt.ifPresent(undying -> {
        AttributeInstance speedAttribute = livingEntity
            .getAttribute(Attributes.MOVEMENT_SPEED);

        if (speedAttribute != null) {
          speedAttribute.removeModifier(SPEED_MOD);
        }

        if (undying.hasSmite()) {

          if (livingEntity.tickCount % 10 == 0) {
            ServerLevel world = (ServerLevel) livingEntity.getCommandSenderWorld();
            world.sendParticles(ParticleTypes.INSTANT_EFFECT, livingEntity.getX(),
                livingEntity.getY() + livingEntity.getBbHeight() / 2.0D, livingEntity.getZ(), 2,
                livingEntity.getBbWidth() / 2.0D, livingEntity.getBbHeight() / 4.0D,
                livingEntity.getBbWidth() / 2.0D, 0.0D);
          }
          undying.tickSmite();
        } else {

          if (livingEntity.tickCount % 20 == 0 && livingEntity.getHealth() < livingEntity
              .getMaxHealth()) {
            livingEntity.heal((float) ConsecrationConfig.healthRegen);
          }
          double speedMod = ConsecrationConfig.speedModifier;

          if (speedMod > 0 && speedAttribute != null
              && speedAttribute.getModifier(SPEED_MOD) == null) {
            speedAttribute.addTransientModifier(new AttributeModifier(SPEED_MOD, "Undead speed", speedMod,
                Operation.MULTIPLY_TOTAL));
          }
        }
      });
    }
  }

  @SubscribeEvent
  public void onPotionAdded(PotionAddedEvent evt) {
    LivingEntity livingEntity = evt.getEntityLiving();

    if (!livingEntity.getCommandSenderWorld().isClientSide && ConsecrationUtils.isUndying(livingEntity)) {
      LazyOptional<IUndying> undyingOpt = UndyingCapability.getCapability(livingEntity);

      undyingOpt.ifPresent(undying -> {
        MobEffectInstance effectInstance = evt.getPotionEffect();
        Set<MobEffect> effect = ConsecrationApi.getHolyRegistry().getHolyEffects();
        MobEffect effect1 = effectInstance.getEffect();
        if (effect.contains(effect1)) {
          int duration = effect1.isInstantenous() ? ConsecrationConfig.CONFIG.holySmiteDuration.get()
              : effectInstance.getDuration();
          undying.setSmiteDuration(duration * 20);
        }
      });
    }
  }

  @SubscribeEvent
  public void onLivingDamage(LivingDamageEvent evt) {
    LivingEntity livingEntity = evt.getEntityLiving();

    if (!livingEntity.getCommandSenderWorld().isClientSide && ConsecrationUtils.isUndying(livingEntity)) {
      LazyOptional<IUndying> undyingOpt = UndyingCapability.getCapability(livingEntity);

      if (!undyingOpt.isPresent() &&
          evt.getSource().getDirectEntity() instanceof LivingEntity attacker) {
        LazyOptional<IUndying> undyingOpt2 = UndyingCapability.getCapability(attacker);

        undyingOpt2.ifPresent(undying -> {
          int level = ConsecrationUtils.protect(attacker, livingEntity, evt.getSource());

          if (level > 0 && livingEntity.getCommandSenderWorld().random.nextFloat() < 0.15F * (float) level) {
            undying.setSmiteDuration(ConsecrationConfig.CONFIG.holySmiteDuration.get() * 20);
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
            undying.setSmiteDuration(ConsecrationConfig.fireSmiteDuration * 20);
          } else {
            undying.setSmiteDuration(ConsecrationConfig.holySmiteDuration * 20);
          }

          if (source.getEntity() instanceof ServerPlayer) {
            SmiteTrigger.INSTANCE.trigger((ServerPlayer) source.getEntity());
          }
        } else if (!source.isBypassMagic() && !undying.hasSmite()) {
          Entity trueSource = source.getEntity();

          if ((trueSource != null && (trueSource instanceof Player
              || ConsecrationConfig.bystanderNerf))) {
            evt.setAmount(evt.getAmount() * (float) (1 - ConsecrationConfig.damageReduction));
          }
        }
      });
    }
  }
}
