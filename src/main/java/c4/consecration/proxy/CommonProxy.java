/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.proxy;

import c4.consecration.common.EventHandlerCommon;
import c4.consecration.common.potions.ModPotions;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class CommonProxy {

    public void preInit(FMLPreInitializationEvent evt) {
    }

    public void init(FMLInitializationEvent evt) {
        MinecraftForge.EVENT_BUS.register(new EventHandlerCommon());
    }

    public void postInit(FMLPostInitializationEvent evt) {
    }

    @SubscribeEvent
    public static void registerPotionTypes(RegistryEvent.Register<PotionType> evt) {
        evt.getRegistry().register(ModPotions.HOLY);
        evt.getRegistry().register(ModPotions.STRONG_HOLY);
        evt.getRegistry().register(ModPotions.ULTIMATE_HOLY);
    }

    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> evt) {
        evt.getRegistry().register(ModPotions.HOLY_POTION);
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> evt) {
        PotionHelper.addMix(PotionTypes.AWKWARD, Ingredient.fromStacks(new ItemStack(Items.GOLDEN_APPLE, 1, 0)), ModPotions.HOLY);
        PotionHelper.addMix(PotionTypes.AWKWARD, Ingredient.fromStacks(new ItemStack(Items.GOLDEN_APPLE, 1, 1)), ModPotions.ULTIMATE_HOLY);
        PotionHelper.addMix(ModPotions.HOLY, Items.GLOWSTONE_DUST, ModPotions.STRONG_HOLY);
    }
}
