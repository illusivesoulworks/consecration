/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.init;

import c4.consecration.common.items.*;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder("consecration")
public class HolderConsecration {

    @ObjectHolder("blessed_trail")
    public static final Block blessedTrail = null;

    @ObjectHolder("fire_stick")
    public static final ItemBase fireStick = null;

    @ObjectHolder("fire_bomb")
    public static final ItemBase fireBomb = null;

    @ObjectHolder("blessed_dust")
    public static final ItemBase blessedDust = null;

    @ObjectHolder("fire_arrow")
    public static final ItemFireArrow fireArrow = null;
}
