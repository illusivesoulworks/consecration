/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.init;

import c4.consecration.Consecration;
import c4.consecration.common.advancements.SmiteKilledTrigger;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.util.ResourceLocation;

public class ConsecrationTriggers {

    public static final SmiteKilledTrigger SMITE_KILLED = new SmiteKilledTrigger(new ResourceLocation
            (Consecration.MODID, "smite_killed"));

    public static void init() {
        CriteriaTriggers.register(SMITE_KILLED);
    }
}
