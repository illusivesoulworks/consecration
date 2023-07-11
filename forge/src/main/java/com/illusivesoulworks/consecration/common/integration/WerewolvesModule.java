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

package com.illusivesoulworks.consecration.common.integration;

import com.illusivesoulworks.consecration.api.ConsecrationApi;
import com.illusivesoulworks.consecration.api.ConsecrationImc;
import java.util.function.BiFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.InterModComms;

public class WerewolvesModule extends AbstractCompatibilityModule {

  @Override
  public void enqueueImc() {
    InterModComms.sendTo("consecration", ConsecrationImc.HOLY_ATTACK.getId(),
        () -> (BiFunction<LivingEntity, DamageSource, Boolean>) (livingEntity, damageSource) -> {

          if (damageSource.getDirectEntity() instanceof LivingEntity) {
            ItemStack stack = ((LivingEntity) damageSource.getDirectEntity())
                .getMainHandItem();

            // Can hardcode this to silver since that's the only relevant oil currently
            // Will need to adapt if the mod adds other materials as oils
            if (ConsecrationApi.getInstance().isHolyMaterial("silver")) {
              CompoundTag tag = stack.getTagElement("weapon_oil");

              if (tag != null) {
                String oil = tag.getString("oil");
                return oil.contains("silver_oil_1") || oil.contains("silver_oil_2");
              }
            }
          }
          return false;
        });
  }
}
