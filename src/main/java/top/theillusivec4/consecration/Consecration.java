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

package top.theillusivec4.consecration;

import com.mojang.logging.LogUtils;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.items.ItemHandlerHelper;
import org.slf4j.Logger;
import top.theillusivec4.consecration.api.ConsecrationApi;
import top.theillusivec4.consecration.api.IUndying;
import top.theillusivec4.consecration.common.ConsecrationConfig;
import top.theillusivec4.consecration.common.HolySources;
import top.theillusivec4.consecration.common.capability.CapabilityEventsHandler;
import top.theillusivec4.consecration.common.impl.ConsecrationApiImpl;
import top.theillusivec4.consecration.common.integration.AbstractCompatibilityModule;
import top.theillusivec4.consecration.common.network.ConsecrationNetwork;
import top.theillusivec4.consecration.common.registry.ConsecrationRegistry;
import top.theillusivec4.consecration.common.trigger.SmiteTrigger;

@Mod(ConsecrationApi.MOD_ID)
public class Consecration {

  public static final Logger LOGGER = LogUtils.getLogger();

  public static final Map<String, Class<? extends AbstractCompatibilityModule>> MODULES =
      new HashMap<>();
  public static final List<AbstractCompatibilityModule> ACTIVE_MODULES = new ArrayList<>();

  public Consecration() {
    ConsecrationApi.setInstance(new ConsecrationApiImpl());
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    ConsecrationRegistry.setup(eventBus);
    eventBus.addListener(this::setup);
    eventBus.addListener(this::registerCaps);
    eventBus.addListener(this::imcEnqueue);
    eventBus.addListener(this::imcProcess);
    eventBus.addListener(this::configLoading);
    eventBus.addListener(this::configReloading);
    ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConsecrationConfig.CONFIG_SPEC);
    MODULES.forEach((id, module) -> {
      if (ModList.get().isLoaded(id)) {
        try {
          ACTIVE_MODULES.add(module.getDeclaredConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
          LOGGER.error("Error adding module for mod " + id);
        }
      }
    });
  }

  private void setup(final FMLCommonSetupEvent evt) {
    ConsecrationNetwork.setup();
    MinecraftForge.EVENT_BUS.register(new CapabilityEventsHandler());
    MinecraftForge.EVENT_BUS.addListener(this::serverStart);
    MinecraftForge.EVENT_BUS.addListener(this::makeCampfireArrow);
    evt.enqueueWork(() -> {
      CriteriaTriggers.register(SmiteTrigger.INSTANCE);
      BrewingRecipeRegistry.addRecipe(
          Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)),
          Ingredient.of(Items.GOLDEN_APPLE),
          PotionUtils.setPotion(new ItemStack(Items.POTION),
              ConsecrationRegistry.HOLY_POTION.get()));
      BrewingRecipeRegistry.addRecipe(Ingredient.of(
              PotionUtils.setPotion(new ItemStack(Items.POTION),
                  ConsecrationRegistry.HOLY_POTION.get())),
          Ingredient.of(Items.REDSTONE), PotionUtils.setPotion(new ItemStack(Items.POTION),
              ConsecrationRegistry.STRONG_HOLY_POTION.get()));
    });
  }

  private void imcEnqueue(final InterModEnqueueEvent evt) {
    ACTIVE_MODULES.forEach(AbstractCompatibilityModule::enqueueImc);
  }

  private void imcProcess(final InterModProcessEvent evt) {
    HolySources.processImc(evt.getIMCStream());
  }

  private void registerCaps(final RegisterCapabilitiesEvent evt) {
    evt.register(IUndying.class);
  }

  private void configLoading(final ModConfigEvent.Loading evt) {

    if (evt.getConfig().getModId().equals(ConsecrationApi.MOD_ID)) {
      ConsecrationConfig.bake();
    }
  }

  private void configReloading(final ModConfigEvent.Reloading evt) {

    if (evt.getConfig().getModId().equals(ConsecrationApi.MOD_ID)) {
      ConsecrationConfig.bake();
    }
  }

  private void serverStart(final ServerAboutToStartEvent evt) {
    HolySources.setup();
  }

  private void makeCampfireArrow(final PlayerInteractEvent.RightClickBlock evt) {
    ItemStack stack = evt.getItemStack();

    if (stack.getItem() == Items.ARROW) {
      Player player = evt.getPlayer();
      Block block = player.getCommandSenderWorld().getBlockState(evt.getPos()).getBlock();

      if (block == Blocks.CAMPFIRE || block == Blocks.SOUL_CAMPFIRE) {
        stack.shrink(1);
        ItemHandlerHelper.giveItemToPlayer(player,
            new ItemStack(ConsecrationRegistry.FIRE_ARROW.get()), player.getInventory().selected);
      }
    }
  }
}
