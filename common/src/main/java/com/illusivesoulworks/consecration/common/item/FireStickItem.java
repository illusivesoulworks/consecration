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

package com.illusivesoulworks.consecration.common.item;

import javax.annotation.Nonnull;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FireStickItem extends Item {

  public FireStickItem() {
    super(new Item.Properties().durability(13));
  }

  public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {

    if (!player.level().isClientSide() && !entity.getType().fireImmune()) {
      stack.hurtAndBreak(1, player,
          damager -> damager.broadcastBreakEvent(InteractionHand.MAIN_HAND));
      entity.setSecondsOnFire(2);
    }
    return false;
  }

  @Nonnull
  @Override
  public InteractionResult interactLivingEntity(@Nonnull ItemStack stack, Player playerIn,
                                                @Nonnull LivingEntity target,
                                                @Nonnull InteractionHand hand) {

    if (!playerIn.level().isClientSide() && !target.getType().fireImmune()) {
      stack.hurtAndBreak(1, playerIn, damager -> damager.broadcastBreakEvent(hand));
      target.setSecondsOnFire(2);
      return InteractionResult.SUCCESS;
    }
    return super.interactLivingEntity(stack, playerIn, target, hand);
  }
}
