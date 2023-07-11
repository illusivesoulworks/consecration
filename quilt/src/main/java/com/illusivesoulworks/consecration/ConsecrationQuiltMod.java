package com.illusivesoulworks.consecration;

import com.illusivesoulworks.consecration.api.ConsecrationApi;
import com.illusivesoulworks.consecration.api.IUndying;
import com.illusivesoulworks.consecration.common.ConsecrationEvents;
import com.illusivesoulworks.consecration.common.registry.ConsecrationRegistry;
import com.illusivesoulworks.consecration.common.trigger.SmiteTrigger;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.networking.api.EntityTrackingEvents;

public class ConsecrationQuiltMod implements ModInitializer {

  @Override
  public void onInitialize(ModContainer modContainer) {
    CriteriaTriggers.register(SmiteTrigger.INSTANCE);
    FabricBrewingRecipeRegistry.registerPotionRecipe(Potions.AWKWARD,
        Ingredient.of(Items.GOLDEN_APPLE), ConsecrationRegistry.HOLY_POTION.get());
    FabricBrewingRecipeRegistry.registerItemRecipe((PotionItem) Items.POTION,
        Ingredient.of(Items.GUNPOWDER), (PotionItem) Items.SPLASH_POTION);
    FabricBrewingRecipeRegistry.registerItemRecipe((PotionItem) Items.POTION,
        Ingredient.of(Items.DRAGON_BREATH), (PotionItem) Items.LINGERING_POTION);
    FabricBrewingRecipeRegistry.registerPotionRecipe(ConsecrationRegistry.HOLY_POTION.get(),
        Ingredient.of(Items.GLOWSTONE_DUST), ConsecrationRegistry.STRONG_HOLY_POTION.get());
    UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
      ConsecrationEvents.createCampfireArrow(player, player.getItemInHand(hand),
          hitResult.getBlockPos(), ((player1, stack) -> player1.getInventory().add(stack)));
      return InteractionResult.PASS;
    });
    EntityTrackingEvents.START_TRACKING.register((trackedEntity, player) -> {

      if (trackedEntity instanceof LivingEntity livingEntity) {
        ConsecrationApi.getInstance().getUndying(livingEntity).ifPresent(IUndying::sync);
      }
    });
  }
}
