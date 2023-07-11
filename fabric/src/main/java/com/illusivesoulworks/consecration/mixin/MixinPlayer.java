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
