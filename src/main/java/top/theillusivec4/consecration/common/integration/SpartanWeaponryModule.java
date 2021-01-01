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

import com.oblivioussp.spartanweaponry.api.IWeaponTraitContainer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiFunction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import top.theillusivec4.consecration.Consecration;
import top.theillusivec4.consecration.api.ConsecrationApi;

public class SpartanWeaponryModule extends AbstractModule {

  private final static Method GET_ARROW_STACK = ObfuscationReflectionHelper
      .findMethod(AbstractArrowEntity.class, "func_184550_j");

  @Override
  public void enqueueImc() {
    InterModComms.sendTo("consecration", "holy_attack",
        () -> (BiFunction<LivingEntity, DamageSource, Boolean>) (livingEntity, damageSource) -> {
          Entity source = damageSource.getImmediateSource();

          if (source instanceof LivingEntity) {
            ItemStack stack = ((LivingEntity) source).getHeldItemMainhand();

            if (stack.getItem() instanceof IWeaponTraitContainer) {
              String name =
                  ((IWeaponTraitContainer<?>) stack.getItem()).getMaterial().getMaterialName();
              return ConsecrationApi.getHolyRegistry().getHolyMaterials().contains(name);
            }
          } else if (source instanceof AbstractArrowEntity) {
            ItemStack stack = ItemStack.EMPTY;

            try {
              stack = (ItemStack) GET_ARROW_STACK.invoke(source);
            } catch (IllegalAccessException | InvocationTargetException e) {
              Consecration.LOGGER.error("Error invoking getArrowStack for " + source);
            }
            ResourceLocation rl = stack.getItem().getRegistryName();

            if (rl != null) {
              String name = rl.getPath();
              return containsHolyMaterial(name);
            }
          }
          return false;
        });
  }

  private static boolean containsHolyMaterial(String value) {

    for (String mat : ConsecrationApi.getHolyRegistry().getHolyMaterials()) {
      String pattern = "arrow_" + mat + "(\\b|[_-]\\w*)";

      if (value.matches(pattern)) {
        return true;
      }
    }
    return false;
  }
}
