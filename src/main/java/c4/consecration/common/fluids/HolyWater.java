/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.fluids;

import c4.consecration.Consecration;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class HolyWater extends Fluid {

    public HolyWater() {
        super("holy_water", new ResourceLocation(Consecration.MODID, "blocks/holy_water_still"),
                new ResourceLocation(Consecration.MODID, "blocks/holy_water_flow"));
        this.setColor(0xffffffff);
        this.setEmptySound(SoundEvents.ITEM_BUCKET_EMPTY);
        this.setFillSound(SoundEvents.ITEM_BUCKET_FILL);
        this.setRarity(EnumRarity.RARE);
        this.setLuminosity(6);
    }
}
