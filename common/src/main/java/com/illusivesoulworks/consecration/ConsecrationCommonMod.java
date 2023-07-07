package com.illusivesoulworks.consecration;

import com.illusivesoulworks.consecration.common.registry.ConsecrationRegistry;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class ConsecrationCommonMod {

  public static void setup() {
    ConsecrationRegistry.init();
  }

  public static void createCampfireArrow(Player player, ItemStack stack, BlockPos pos,
                                         BiConsumer<Player, ItemStack> giver) {

    if (stack.getItem() == Items.ARROW) {
      Block block = player.level.getBlockState(pos).getBlock();

      if (block == Blocks.CAMPFIRE || block == Blocks.SOUL_CAMPFIRE) {
        stack.shrink(1);
        giver.accept(player, new ItemStack(ConsecrationRegistry.FIRE_ARROW.get()));
      }
    }
  }
}