/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.integrations;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.Map;

public abstract class ModuleCompatibility {

    public static Map<String, Class<? extends ModuleCompatibility>> compatDeps = new HashMap<>();
    public static Map<String, ModuleCompatibility> loadedMods = new HashMap<>();

    protected String modid;

    static {
        compatDeps.put("xreliquary", ModuleReliquary.class);
        compatDeps.put("toolbox", ModuleToolbox.class);
        compatDeps.put("tconstruct", ModuleTConstruct.class);
        compatDeps.put("spartanweaponry", ModuleSpartanWeaponry.class);
        compatDeps.put("conarm", ModuleConArm.class);
        compatDeps.put("mkultra", ModuleMKUltra.class);
        compatDeps.put("tetra", ModuleTetra.class);
        compatDeps.put("silentgear", ModuleSilents.class);
    }

    public ModuleCompatibility(String modid) {
        this(modid, false);
    }

    public ModuleCompatibility(String modid, boolean hasSubscriptions) {
        this.modid = modid;
        loadedMods.putIfAbsent(modid, this);
        if (hasSubscriptions) {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    public boolean process(EntityLivingBase target, DamageSource source) {
        return false;
    }

    public boolean processArmor(EntityLivingBase wearer, ItemStack stack, DamageSource source) { return false; }

    public static Map<String, ModuleCompatibility> getLoadedMods() {
        return loadedMods;
    }
}
