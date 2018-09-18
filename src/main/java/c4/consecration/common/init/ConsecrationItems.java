/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.init;

import c4.consecration.Consecration;
import c4.consecration.common.items.*;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.*;

@Mod.EventBusSubscriber(modid = Consecration.MODID)
@ObjectHolder("consecration")
public class ConsecrationItems {

    @ObjectHolder("fire_stick")
    public static final ItemBase fireStick = null;

    @ObjectHolder("fire_bomb")
    public static final ItemBase fireBomb = null;

    @ObjectHolder("blessed_dust")
    public static final ItemBase blessedDust = null;

    @ObjectHolder("fire_arrow")
    public static final ItemFireArrow fireArrow = null;

    @SubscribeEvent
    public static void init(RegistryEvent.Register<Item> evt) {
        evt.getRegistry().registerAll(
                new ItemBlessedDust(),
                new ItemFireArrow(),
                new ItemFireBomb(),
                new ItemFireStick());
    }
}
