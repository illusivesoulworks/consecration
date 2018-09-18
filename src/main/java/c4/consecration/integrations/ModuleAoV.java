/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.integrations;

import c4.consecration.common.util.UndeadRegistry;

public class ModuleAoV extends ModuleCompatibility {

    public ModuleAoV() {
        super("aov");
    }

    @Override
    public void register() {
        UndeadRegistry.addHolyDamage("aov.nimbusray");
    }
}
