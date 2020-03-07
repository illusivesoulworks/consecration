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

package top.theillusivec4.consecration.common.entity;

import javax.annotation.Nonnull;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import top.theillusivec4.consecration.common.registry.ConsecrationRegistry;

public class FireArrowEntity extends ArrowEntity {

  public FireArrowEntity(EntityType<? extends ArrowEntity> type, World world) {
    super(type, world);
  }

  public FireArrowEntity(World worldIn, double x, double y, double z) {
    super(worldIn, x, y, z);
  }

  public FireArrowEntity(World worldIn, LivingEntity shooter) {
    super(worldIn, shooter);
  }

  @Override
  public void tick() {
    super.tick();
    this.setFire(100);
  }

  @Override
  @Nonnull
  protected ItemStack getArrowStack() {
    return new ItemStack(ConsecrationRegistry.FIRE_ARROW);
  }
}
