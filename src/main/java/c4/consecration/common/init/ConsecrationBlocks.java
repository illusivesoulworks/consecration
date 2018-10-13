/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.init;

import c4.consecration.Consecration;
import c4.consecration.common.blocks.BlockBlessedTrail;
import c4.consecration.common.blocks.BlockHolyWater;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber(modid = Consecration.MODID)
@GameRegistry.ObjectHolder("consecration")
public class ConsecrationBlocks {

    @GameRegistry.ObjectHolder("blessed_trail")
    public static final Block blessedTrail = null;

    @GameRegistry.ObjectHolder("holy_water")
    public static final Block holyWater = null;

    @SubscribeEvent
    public static void init(RegistryEvent.Register<Block> evt) {
        evt.getRegistry().registerAll(new BlockBlessedTrail(), new BlockHolyWater());
    }
}
