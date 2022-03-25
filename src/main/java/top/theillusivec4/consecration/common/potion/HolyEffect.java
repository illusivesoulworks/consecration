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

package top.theillusivec4.consecration.common.potion;

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
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.consecration.api.ConsecrationApi;
import top.theillusivec4.consecration.common.ConsecrationUtils;
import top.theillusivec4.consecration.common.capability.UndyingCapability;
import top.theillusivec4.consecration.common.capability.UndyingCapability.IUndying;
import top.theillusivec4.consecration.common.registry.RegistryReference;

public class HolyEffect extends MobEffect {

  public HolyEffect() {
    super(MobEffectCategory.BENEFICIAL, 0xFFFFFF);
    this.setRegistryName(RegistryReference.HOLY);
  }

  @Override
  public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity indirectSource,
                                      @Nonnull LivingEntity livingEntity, int amplifier,
                                      double health) {

    if (livingEntity instanceof ZombieVillager) {
      convertZombieVillager((ZombieVillager) livingEntity, indirectSource, 1800 >> amplifier);
    } else {

      if (ConsecrationUtils.isUndying(livingEntity)) {
        LazyOptional<IUndying> undyingOpt = UndyingCapability.getCapability(livingEntity);
        undyingOpt.ifPresent(undying -> {
          if (source == null) {
            livingEntity.hurt(ConsecrationApi.getHolyRegistry().causeHolyDamage(),
                (float) (8 << amplifier));
          } else {
            livingEntity.hurt(
                ConsecrationApi.getHolyRegistry().causeIndirectHolyDamage(source, indirectSource),
                (float) (8 << amplifier));
          }
        });
      } else {
        livingEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, amplifier));
        livingEntity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, amplifier));
      }
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
    zombieVillager.startConverting(uuid, conversionTime);
  }
}
