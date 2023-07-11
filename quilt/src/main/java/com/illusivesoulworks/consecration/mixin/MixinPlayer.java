package com.illusivesoulworks.consecration.mixin;

import com.illusivesoulworks.consecration.common.ConsecrationEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Player.class)
public class MixinPlayer {

  @ModifyVariable(at = @At(value = "INVOKE", target = "net/minecraft/world/entity/player/Player.awardStat(Lnet/minecraft/resources/ResourceLocation;I)V", shift = At.Shift.BY, by = 2), method = "actuallyHurt", ordinal = 0, argsOnly = true)
  private float consecration$actuallyHurt(float damageAmount, DamageSource damageSource,
                                          float unused) {
    return ConsecrationEvents.onDamaged((Player) (Object) this, damageSource, damageAmount);
  }
}
