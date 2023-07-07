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

package com.illusivesoulworks.consecration.common.effect;

import com.illusivesoulworks.consecration.api.ConsecrationApi;
import com.illusivesoulworks.consecration.api.IUndying;
import com.illusivesoulworks.consecration.mixin.AccessorZombieVillager;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;

public class HolyEffect extends MobEffect {

  public HolyEffect() {
    super(MobEffectCategory.BENEFICIAL, 0xFFFFFF);
  }

  @Override
  public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity indirectSource,
                                      @Nonnull LivingEntity livingEntity, int amplifier,
                                      double health) {

    if (livingEntity instanceof ZombieVillager) {
      convertZombieVillager((ZombieVillager) livingEntity, indirectSource, 1800 >> amplifier);
    } else {
      Optional<IUndying> maybeUndying = ConsecrationApi.getInstance().getUndying(livingEntity);
      maybeUndying.ifPresentOrElse(undying -> {
        if (source == null) {
          livingEntity.hurt(ConsecrationApi.getInstance().causeHolyDamage(),
              (float) (8 << amplifier));
        } else {
          livingEntity.hurt(
              ConsecrationApi.getInstance().causeIndirectHolyDamage(source, indirectSource),
              (float) (8 << amplifier));
        }
      }, () -> {
        livingEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, amplifier));
        livingEntity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, amplifier));
      });
    }
  }

  @Override
  public boolean isInstantenous() {
    return true;
  }

  private void convertZombieVillager(ZombieVillager zombieVillager, @Nullable Entity source,
                                     int conversionTime) {

    if (zombieVillager.isConverting()) {
      return;
    }
    UUID uuid = source instanceof Player ? source.getUUID() : null;
    ((AccessorZombieVillager) zombieVillager).callStartConverting(uuid, conversionTime);
  }
}
