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
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import top.theillusivec4.consecration.common.registry.RegistryReference;

public class FireStickItem extends Item {

  public FireStickItem() {
    super(new Item.Properties().maxDamage(13).group(ItemGroup.COMBAT));
    this.setRegistryName(RegistryReference.FIRE_STICK);
  }

  @Override
  public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {

    if (!player.world.isRemote && !entity.getType().isImmuneToFire()) {
      stack.damageItem(1, player, damager -> damager.sendBreakAnimation(Hand.MAIN_HAND));
      entity.setFire(2);
    }
    return false;
  }

  @Nonnull
  @Override
  public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn,
      LivingEntity target, Hand hand) {

    if (!playerIn.world.isRemote && !target.getType().isImmuneToFire()) {
      stack.damageItem(1, playerIn, damager -> damager.sendBreakAnimation(hand));
      target.setFire(2);
      return ActionResultType.SUCCESS;
    }
    return super.itemInteractionForEntity(stack, playerIn, target, hand);
  }
}
