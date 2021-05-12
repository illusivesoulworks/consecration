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
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.InterModComms;
import se.mickelus.tetra.items.modular.IModularItem;
import top.theillusivec4.consecration.api.ConsecrationApi;

public class TetraModule extends AbstractModule {

  @Override
  public void enqueueImc() {
    InterModComms.sendTo("consecration", "holy_attack",
        () -> (BiFunction<LivingEntity, DamageSource, Boolean>) (livingEntity, damageSource) -> {

          if (damageSource.getImmediateSource() instanceof LivingEntity) {
            ItemStack stack = ((LivingEntity) damageSource.getImmediateSource())
                .getHeldItemMainhand();

            if (stack.getItem() instanceof IModularItem && stack.hasTag()) {
              CompoundNBT compound = stack.getTag();
              assert compound != null;
              for (String key : compound.keySet()) {

                if (key.contains("_material")) {
                  String value = compound.getString(key);

                  if (containsHolyMaterial(value)) {
                    return true;
                  }
                }
              }
            }
          }
          return false;
        });
  }

  private static boolean containsHolyMaterial(String value) {

    for (String mat : ConsecrationApi.getHolyRegistry().getHolyMaterials()) {
      String pattern = "\\w*/" + mat + "(\\b|[_-]\\w*)";

      if (value.matches(pattern)) {
        return true;
      }
    }
    return false;
  }
}
