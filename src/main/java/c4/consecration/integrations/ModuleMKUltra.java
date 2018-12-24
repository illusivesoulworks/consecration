/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.integrations;

import c4.consecration.common.config.ConfigHandler;
import com.chaosbuffalo.mkultra.core.MKDamageSource;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

public class ModuleMKUltra extends ModuleCompatibility {

    public ModuleMKUltra() {
        super("mkultra");
    }

    @Override
    public boolean process(EntityLivingBase target, DamageSource source) {

        if (source instanceof MKDamageSource) {
            String ability = ((MKDamageSource) source).getAbilityId().getPath();

            for (String s : ConfigHandler.modSupport.mkultraSources) {

                if (s.equals(ability)) {
                    return true;
                }
            }
        }
        return false;
    }
}
