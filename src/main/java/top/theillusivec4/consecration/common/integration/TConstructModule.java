/*
 * Copyright (c) 2020 C4
 *
 * This file is part of Consecration - Compatibility Add-on, a mod
 * made for Minecraft.
 *
 * Consecration - Compatibility Add-on is free software: you can
 * redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * Consecration - Compatibility Add-on is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR PARTICULAR PURPOSE.  See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with Consecration - Compatibility
 * Add-on. If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.consecration.common.integration;

import java.util.function.BiFunction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.InterModComms;
import slimeknights.tconstruct.library.tools.item.ToolItem;
import top.theillusivec4.consecration.api.ConsecrationApi;

public class TConstructModule extends AbstractModule {

  @Override
  public void enqueueImc() {
    InterModComms.sendTo("consecration", "holy_attack",
        () -> (BiFunction<LivingEntity, DamageSource, Boolean>) (livingEntity, damageSource) -> {
          Entity source = damageSource.getImmediateSource();

          if (source instanceof LivingEntity) {
            ItemStack stack = ((LivingEntity) source).getHeldItemMainhand();

            if (stack.getItem() instanceof ToolItem && stack.getTag() != null) {

              for (INBT tic_material : stack.getTag()
                  .getList("tic_materials", Constants.NBT.TAG_STRING)) {

                if (containsHolyMaterial(tic_material.getString().split(":")[1])) {
                  return true;
                }
              }
            }
          }
          return false;
        });
  }

  private static boolean containsHolyMaterial(String value) {

    for (String mat : ConsecrationApi.getHolyRegistry().getHolyMaterials()) {
      String pattern = "\\b" + mat + "(\\b|[_-]\\w*)";

      if (value.matches(pattern)) {
        return true;
      }
    }
    return false;
  }
}
