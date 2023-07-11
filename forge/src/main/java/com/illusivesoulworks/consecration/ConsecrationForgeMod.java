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

package com.illusivesoulworks.consecration;

import com.illusivesoulworks.consecration.api.ConsecrationImc;
import com.illusivesoulworks.consecration.api.IUndying;
import com.illusivesoulworks.consecration.common.ConsecrationEvents;
import com.illusivesoulworks.consecration.common.capability.UndyingCapability;
import com.illusivesoulworks.consecration.common.config.ConsecrationConfig;
import com.illusivesoulworks.consecration.common.impl.HolySources;
import com.illusivesoulworks.consecration.common.integration.AbstractCompatibilityModule;
import com.illusivesoulworks.consecration.common.integration.SilentGearModule;
import com.illusivesoulworks.consecration.common.integration.TetraModule;
import com.illusivesoulworks.consecration.common.integration.WerewolvesModule;
import com.illusivesoulworks.consecration.common.network.ConsecrationNetwork;
import com.illusivesoulworks.consecration.common.registry.ConsecrationRegistry;
import com.illusivesoulworks.consecration.common.trigger.SmiteTrigger;
import com.mojang.logging.LogUtils;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.items.ItemHandlerHelper;
import org.slf4j.Logger;

@Mod(ConsecrationConstants.MOD_ID)
public class ConsecrationForgeMod {

  public static final Logger LOGGER = LogUtils.getLogger();

  public static final Map<String, Class<? extends AbstractCompatibilityModule>> MODULES =
      new HashMap<>();
  public static final List<AbstractCompatibilityModule> ACTIVE_MODULES = new ArrayList<>();

  static {
    MODULES.put("tetra", TetraModule.class);
    MODULES.put("werewolves", WerewolvesModule.class);
    MODULES.put("silentgear", SilentGearModule.class);
  }

  public ConsecrationForgeMod() {
    ConsecrationConfig.setup();
    ConsecrationCommonMod.setup();
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    eventBus.addListener(this::setup);
    eventBus.addListener(this::registerCaps);
    eventBus.addListener(this::imcEnqueue);
    eventBus.addListener(this::imcProcess);
    eventBus.addListener(this::creativeTab);
    MODULES.forEach((id, module) -> {
      if (ModList.get().isLoaded(id)) {
        try {
          ACTIVE_MODULES.add(module.getDeclaredConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
          LOGGER.error("Error adding module for mod " + id);
        }
      }
    });
  }

  private void setup(final FMLCommonSetupEvent evt) {
    ConsecrationNetwork.setup();
    MinecraftForge.EVENT_BUS.register(new UndyingCapability());
    MinecraftForge.EVENT_BUS.addListener(this::makeCampfireArrow);
    evt.enqueueWork(() -> {
      CriteriaTriggers.register(SmiteTrigger.INSTANCE);
      BrewingRecipeRegistry.addRecipe(
          Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)),
          Ingredient.of(Items.GOLDEN_APPLE), PotionUtils.setPotion(new ItemStack(Items.POTION),
              ConsecrationRegistry.HOLY_POTION.get()));
      BrewingRecipeRegistry.addRecipe(Ingredient.of(
              PotionUtils.setPotion(new ItemStack(Items.POTION),
                  ConsecrationRegistry.HOLY_POTION.get())), Ingredient.of(Items.GLOWSTONE_DUST),
          PotionUtils.setPotion(new ItemStack(Items.POTION),
              ConsecrationRegistry.STRONG_HOLY_POTION.get()));
    });
  }

  private void imcEnqueue(final InterModEnqueueEvent evt) {
    ACTIVE_MODULES.forEach(AbstractCompatibilityModule::enqueueImc);
  }

  private void imcProcess(final InterModProcessEvent evt) {
    evt.getIMCStream().forEach(imcMessage -> {
      Object message = imcMessage.messageSupplier().get();
      String method = imcMessage.method();

      if (message instanceof BiFunction) {

        if (method.equals(ConsecrationImc.HOLY_ATTACK.getId())) {
          HolySources.addHolyAttack((BiFunction<LivingEntity, DamageSource, Boolean>) message);
        } else if (method.equals(ConsecrationImc.HOLY_PROTECTION.getId())) {
          HolySources.addHolyProtection((BiFunction<LivingEntity, DamageSource, Integer>) message);
        }
      }
    });
  }

  private void registerCaps(final RegisterCapabilitiesEvent evt) {
    evt.register(IUndying.class);
  }

  private void makeCampfireArrow(final PlayerInteractEvent.RightClickBlock evt) {
    ConsecrationEvents.createCampfireArrow(evt.getEntity(), evt.getItemStack(), evt.getPos(),
        (player, stack) -> ItemHandlerHelper.giveItemToPlayer(player, stack,
            player.getInventory().selected));
  }

  private void creativeTab(final BuildCreativeModeTabContentsEvent evt) {
    ResourceKey<CreativeModeTab> tab = evt.getTabKey();

    if (tab == CreativeModeTabs.COMBAT) {
      evt.accept(ConsecrationRegistry.FIRE_STICK.get());
      evt.accept(ConsecrationRegistry.FIRE_ARROW.get());
    }
  }
}
