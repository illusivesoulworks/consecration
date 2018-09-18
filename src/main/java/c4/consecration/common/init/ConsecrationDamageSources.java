/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.init;

import net.minecraft.util.DamageSource;

public class ConsecrationDamageSources {

    public static final DamageSource HOLY = new DamageSource("holy").setMagicDamage().setDamageBypassesArmor();
}
