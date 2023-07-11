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

package com.illusivesoulworks.consecration.mixin;

import com.illusivesoulworks.consecration.api.ConsecrationApi;
import com.illusivesoulworks.consecration.api.IUndying;
import com.illusivesoulworks.consecration.common.ConsecrationEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

  @Inject(at = @At("TAIL"), method = "baseTick")
  private void consecration$baseTick(CallbackInfo ci) {
    ConsecrationApi.getInstance().getUndying((LivingEntity) (Object) this)
        .ifPresent(IUndying::tick);
  }

  @ModifyVariable(at = @At(value = "INVOKE", target = "net/minecraft/server/level/ServerPlayer.awardStat(Lnet/minecraft/resources/ResourceLocation;I)V", shift = At.Shift.BY, by = 2), method = "actuallyHurt", ordinal = 0, argsOnly = true)
  private float consecration$actuallyHurt(float damageAmount, DamageSource damageSource,
                                          float unused) {
    return ConsecrationEvents.onDamaged((LivingEntity) (Object) this, damageSource, damageAmount);
  }

  @Inject(at = @At(value = "INVOKE", target = "net/minecraft/world/effect/MobEffectInstance.getEffect()Lnet/minecraft/world/effect/MobEffect;"), method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z")
  private void consecration$addEffect(MobEffectInstance effectInstance, @Nullable Entity entity,
                                      CallbackInfoReturnable<Boolean> cir) {
    ConsecrationApi.getInstance().getUndying((LivingEntity) (Object) this)
        .ifPresent(undying -> undying.onEffectAdded(effectInstance));
  }
}
