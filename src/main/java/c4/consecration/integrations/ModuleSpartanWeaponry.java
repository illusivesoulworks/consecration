/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.integrations;

import c4.consecration.common.util.UndeadHelper;
import com.oblivioussp.spartanweaponry.api.IWeaponPropertyContainer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class ModuleSpartanWeaponry extends ModuleCompatibility {

    public ModuleSpartanWeaponry() {
        super("spartanweaponry");
    }

    @Override
    public boolean process(EntityLivingBase target, DamageSource source) {

        if (source.getTrueSource() instanceof EntityLivingBase) {
            ItemStack stack = ((EntityLivingBase) source.getTrueSource()).getHeldItemMainhand();

            if (stack.getItem() instanceof IWeaponPropertyContainer) {
                String name = ((IWeaponPropertyContainer) stack.getItem()).getMaterialEx().getUnlocName();
                return UndeadHelper.isHolyMaterial(name);
            }
        }
        return false;
    }
}
