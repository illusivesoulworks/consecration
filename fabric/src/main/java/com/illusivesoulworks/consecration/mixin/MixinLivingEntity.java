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

  @ModifyVariable(at = @At(value = "INVOKE", target = "net/minecraft/server/level/ServerPlayer.awardStat(Lnet/minecraft/resources/ResourceLocation;I)V", shift = At.Shift.AFTER), method = "actuallyHurt", ordinal = 0, argsOnly = true, print = true)
  private float consecration$actuallyHurt(float damageAmount, DamageSource damageSource,
                                          float unused) {
    return ConsecrationEvents.onDamaged((LivingEntity) (Object) this, damageSource, damageAmount);
  }

  @Inject(at = @At(value = "INVOKE_ASSIGN", target = "java/util/Map.get(Ljava/lang/Object;)Ljava/lang/Object;"), method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z")
  private void consecration$addEffect(MobEffectInstance effectInstance, @Nullable Entity entity,
                                      CallbackInfoReturnable<Boolean> cir) {
    ConsecrationApi.getInstance().getUndying((LivingEntity) (Object) this)
        .ifPresent(undying -> undying.onEffectAdded(effectInstance));
  }
}
