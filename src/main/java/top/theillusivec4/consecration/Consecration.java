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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.consecration.api.ConsecrationApi;
import top.theillusivec4.consecration.client.ConsecrationRenderer;
import top.theillusivec4.consecration.common.ConsecrationConfig;
import top.theillusivec4.consecration.common.ConsecrationSeed;
import top.theillusivec4.consecration.common.HolyRegistry;
import top.theillusivec4.consecration.common.capability.UndyingCapability;
import top.theillusivec4.consecration.common.integration.*;
import top.theillusivec4.consecration.common.registry.ConsecrationRegistry;
import top.theillusivec4.consecration.common.trigger.SmiteTrigger;

@Mod(Consecration.MODID)
public class Consecration {

  public static final String MODID = "consecration";
  public static final Logger LOGGER = LogManager.getLogger();

  public static final Map<String, Class<? extends AbstractModule>> MODULES = new HashMap<>();
  public static final List<AbstractModule> ACTIVE_MODULES = new ArrayList<>();

  static {
    MODULES.put("tetra", TetraModule.class);
    MODULES.put("spartanweaponry", SpartanWeaponryModule.class);
    MODULES.put("silentgear", SilentGearModule.class);
    MODULES.put("tconstruct", TConstructModule.class);
  }

  public Consecration() {
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    eventBus.addListener(this::setup);
    eventBus.addListener(this::clientSetup);
    eventBus.addListener(this::imcEnqueue);
    eventBus.addListener(this::imcProcess);
    eventBus.addListener(this::config);
    ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConsecrationConfig.CONFIG_SPEC);
    MODULES.forEach((id, module) -> {
      if (ModList.get().isLoaded(id)) {
        try {
          ACTIVE_MODULES.add(module.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
          LOGGER.error("Error adding module for mod " + id);
        }
      }
    });
  }

  private void setup(final FMLCommonSetupEvent evt) {
    UndyingCapability.register();
    CriteriaTriggers.register(SmiteTrigger.INSTANCE);
    BrewingRecipeRegistry.addRecipe(Ingredient
            .fromStacks(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.AWKWARD)),
        Ingredient.fromItems(Items.GOLDEN_APPLE), PotionUtils
            .addPotionToItemStack(new ItemStack(Items.POTION), ConsecrationRegistry.HOLY_POTION));
    BrewingRecipeRegistry.addRecipe(Ingredient.fromStacks(PotionUtils
            .addPotionToItemStack(new ItemStack(Items.POTION), ConsecrationRegistry.HOLY_POTION)),
        Ingredient.fromItems(Items.REDSTONE), PotionUtils
            .addPotionToItemStack(new ItemStack(Items.POTION),
                ConsecrationRegistry.STRONG_HOLY_POTION));
    MinecraftForge.EVENT_BUS.register(this);
  }

  private void clientSetup(final FMLClientSetupEvent evt) {
    ConsecrationRenderer.register();
  }

  private void imcEnqueue(final InterModEnqueueEvent evt) {
    ACTIVE_MODULES.forEach(AbstractModule::enqueueImc);
  }

  private void imcProcess(final InterModProcessEvent evt) {
    ConsecrationSeed.registerImc(evt.getIMCStream());
  }

  private void config(final ModConfigEvent evt) {

    if (evt.getConfig().getModId().equals(MODID)) {
      ConsecrationConfig.bake();
    }
  }

  @SubscribeEvent
  public void serverStart(final FMLServerAboutToStartEvent evt) {
    ConsecrationApi.setHolyRegistry(new HolyRegistry());
    ConsecrationSeed.fillRegistry();
  }

  @SubscribeEvent
  public void serverStopped(final FMLServerStoppedEvent evt) {
    ConsecrationApi.setHolyRegistry(null);
  }
}
