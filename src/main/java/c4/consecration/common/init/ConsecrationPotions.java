/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.init;

import c4.consecration.Consecration;
import c4.consecration.common.potions.HolyPotion;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Consecration.MODID)
public class ConsecrationPotions {

    public static final Potion HOLY_POTION = new HolyPotion().setBeneficial().setPotionName("effect.holy").setRegistryName("holy_potion");
    public static final PotionType HOLY = new PotionType("holy", new PotionEffect(HOLY_POTION, 1, 0)).setRegistryName
            ("holy");
    public static final PotionType STRONG_HOLY = new PotionType("holy", new PotionEffect(HOLY_POTION, 1, 1))
            .setRegistryName("strong_holy");

    @SubscribeEvent
    public static void initPotionTypes(RegistryEvent.Register<PotionType> evt) {
        evt.getRegistry().registerAll(
                HOLY,
                STRONG_HOLY);
    }

    @SubscribeEvent
    public static void initPotions(RegistryEvent.Register<Potion> evt) {
        evt.getRegistry().register(HOLY_POTION);
    }
}
