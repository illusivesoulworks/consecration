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

package top.theillusivec4.consecration.common.item;

import javax.annotation.Nonnull;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import top.theillusivec4.consecration.common.entity.FireArrowEntity;
import top.theillusivec4.consecration.common.registry.RegistryReference;

public class FireArrowItem extends ArrowItem {

  public FireArrowItem() {
    super(new Item.Properties().tab(CreativeModeTab.TAB_COMBAT));
    this.setRegistryName(RegistryReference.FIRE_ARROW);
  }

  @Nonnull
  @Override
  public AbstractArrow createArrow(@Nonnull Level worldIn, @Nonnull ItemStack stack,
      @Nonnull LivingEntity shooter) {
    return new FireArrowEntity(worldIn, shooter);
  }
}
