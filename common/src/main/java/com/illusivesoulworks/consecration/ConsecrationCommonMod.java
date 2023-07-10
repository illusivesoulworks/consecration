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
}