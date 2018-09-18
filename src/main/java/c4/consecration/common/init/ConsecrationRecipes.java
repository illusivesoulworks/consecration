/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.init;

import c4.consecration.Consecration;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Consecration.MODID)
public class ConsecrationRecipes {

    @SubscribeEvent
    public static void init(RegistryEvent.Register<IRecipe> evt) {
        PotionHelper.addMix(PotionTypes.AWKWARD, Ingredient.fromStacks(new ItemStack(Items.GOLDEN_APPLE, 1, 0)), ConsecrationPotions.HOLY);
        PotionHelper.addMix(PotionTypes.AWKWARD, Ingredient.fromStacks(new ItemStack(Items.GOLDEN_APPLE, 1, 1)), ConsecrationPotions.ULTIMATE_HOLY);
        PotionHelper.addMix(ConsecrationPotions.HOLY, Items.GLOWSTONE_DUST, ConsecrationPotions.STRONG_HOLY);
    }
}
