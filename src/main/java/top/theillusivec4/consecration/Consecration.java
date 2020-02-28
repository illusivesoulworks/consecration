/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package top.theillusivec4.consecration;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.consecration.common.ConsecrationConfig;
import top.theillusivec4.consecration.common.capability.UndyingCapability;
import top.theillusivec4.consecration.common.registry.ConsecrationRegistry;
import top.theillusivec4.consecration.common.util.HolyResources;

@Mod(Consecration.MODID)
public class Consecration {

  public static final String MODID = "consecration";
  public static final Logger LOGGER = LogManager.getLogger();

  public Consecration() {
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    eventBus.addListener(this::setup);
    ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConsecrationConfig.serverSpec);
  }

  private void setup(final FMLCommonSetupEvent evt) {
    UndyingCapability.register();
    HolyResources.seedConfigs();
    BrewingRecipeRegistry.addRecipe(Ingredient
            .fromStacks(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.AWKWARD)),
        Ingredient.fromItems(Items.GOLDEN_APPLE), PotionUtils
            .addPotionToItemStack(new ItemStack(Items.POTION), ConsecrationRegistry.HOLY_POTION));
    BrewingRecipeRegistry.addRecipe(Ingredient.fromStacks(PotionUtils
            .addPotionToItemStack(new ItemStack(Items.POTION), ConsecrationRegistry.HOLY_POTION)),
        Ingredient.fromItems(Items.REDSTONE), PotionUtils
            .addPotionToItemStack(new ItemStack(Items.POTION),
                ConsecrationRegistry.STRONG_HOLY_POTION));
  }
}
