/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.proxy;

import c4.consecration.common.entities.EntityFireBomb;
import c4.consecration.init.ModBlocks;
import c4.consecration.init.ModItems;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent evt) {
        super.preInit(evt);
    }

    @Override
    public void init(FMLInitializationEvent evt) {
        super.init(evt);
    }

    @Override
    public void postInit(FMLPostInitializationEvent evt) {
        super.postInit(evt);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent evt) {

        ModItems.blessedDust.initModel();
        ModItems.fireStick.initModel();
        ModItems.fireBomb.initModel();
        ModItems.fireArrow.initModel();

        ModBlocks.hallowedGrounds.initModel();

        EntityFireBomb.initModel();
    }
}