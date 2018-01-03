/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.trading;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import java.util.Random;

public class ListPotionForEmeralds implements EntityVillager.ITradeList
{
    /** The potion that is being bought for emeralds */
    public PotionType potionToBuy;
    /**
     * The price info for the amount of emeralds to sell for, or if negative, the amount of the item to buy for
     * an emerald.
     */
    public EntityVillager.PriceInfo priceInfo;

    public ListPotionForEmeralds(PotionType potion, EntityVillager.PriceInfo priceInfo)
    {
        this.potionToBuy = potion;
        this.priceInfo = priceInfo;
    }

    @Override
    public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random)
    {
        int i = 1;

        if (this.priceInfo != null)
        {
            i = this.priceInfo.getPrice(random);
        }

        ItemStack stack;
        ItemStack potion = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), this.potionToBuy);;

        if (i < 0)
        {
            stack = new ItemStack(Items.EMERALD);
        }
        else
        {
            stack = new ItemStack(Items.EMERALD, i, 0);
        }

        recipeList.add(new MerchantRecipe(stack, potion));
    }
}
