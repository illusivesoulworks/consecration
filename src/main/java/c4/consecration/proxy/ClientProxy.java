/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.proxy;

import c4.consecration.client.render.RenderFireArrow;
import c4.consecration.client.render.RenderFireBomb;
import c4.consecration.common.entities.EntityFireArrow;
import c4.consecration.common.entities.EntityFireBomb;
import c4.consecration.common.init.ConsecrationItems;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy implements IProxy {

    @SubscribeEvent
    @SuppressWarnings("ConstantConditions")
    public static void registerModels(ModelRegistryEvent evt) {

        ConsecrationItems.blessedDust.initModel();
        ConsecrationItems.fireStick.initModel();
        ConsecrationItems.fireBomb.initModel();
        ConsecrationItems.fireArrow.initModel();

        RenderingRegistry.registerEntityRenderingHandler(EntityFireBomb.class, RenderFireBomb.FACTORY);
        RenderingRegistry.registerEntityRenderingHandler(EntityFireArrow.class, RenderFireArrow.FACTORY);
    }
}