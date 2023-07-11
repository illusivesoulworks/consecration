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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.InterModComms;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.part.PartDataList;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

public class SilentGearModule extends AbstractCompatibilityModule {

  @Override
  public void enqueueImc() {
    InterModComms.sendTo("consecration", ConsecrationImc.HOLY_ATTACK.getId(),
        () -> (BiFunction<LivingEntity, DamageSource, Boolean>) (livingEntity, damageSource) -> {

          if (damageSource.getDirectEntity() instanceof LivingEntity) {
            ItemStack stack = ((LivingEntity) damageSource.getDirectEntity())
                .getMainHandItem();
            return containsHolyMaterial(stack);
          }
          return false;
        });
    InterModComms.sendTo("consecration", ConsecrationImc.HOLY_PROTECTION.getId(),
        () -> (BiFunction<LivingEntity, DamageSource, Integer>) (livingEntity, damageSource) -> {
          int level = 0;

          for (ItemStack stack : livingEntity.getArmorSlots()) {

            if (containsHolyMaterial(stack)) {
              level++;
            }
          }
          return level;
        });
  }

  private static boolean containsHolyMaterial(ItemStack stack) {

    if (GearHelper.isGear(stack)) {
      PartDataList data = GearData.getConstructionParts(stack);

      for (PartData partData : data) {

        for (IMaterialInstance material : partData.getMaterials()) {

          if (ConsecrationApi.getInstance().isHolyMaterial(material.getId().getPath())) {
            return true;
          }
        }
      }
    }
    return false;
  }
}
