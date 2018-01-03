/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.init;

import c4.consecration.common.potions.HolyPotion;
import c4.consecration.common.potions.SmitePotion;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.DamageSource;

public class ModPotions {

    public static final DamageSource HOLY_DAMAGE = (new DamageSource("holy")).setDamageBypassesArmor().setMagicDamage();
    public static final Potion HOLY_POTION = new HolyPotion().setBeneficial().setPotionName("effect.holy").setRegistryName("holy_potion");
    public static final Potion SMITE_POTION = new SmitePotion().setPotionName("effect.smite").setRegistryName("smite_potion");
    public static final PotionType HOLY = new PotionType("holy", new PotionEffect(HOLY_POTION, 1)).setRegistryName("holy");
    public static final PotionType STRONG_HOLY = new PotionType("holy", new PotionEffect(HOLY_POTION, 1, 1)).setRegistryName("strong_holy");
    public static final PotionType ULTIMATE_HOLY = new PotionType("holy", new PotionEffect(HOLY_POTION, 1, 2)).setRegistryName("ultimate_holy");
}
