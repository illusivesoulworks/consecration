/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.api;

import c4.consecration.Consecration;
import c4.consecration.init.DamageSourcesConsecration;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import org.apache.logging.log4j.Level;

import java.util.*;

public class UndeadRegistry {

    private static Set<ResourceLocation> undeadList = new HashSet<>();
    private static Set<Potion> holyPotions = new HashSet<>();
    private static Set<ResourceLocation> holyEntities = new HashSet<>();
    private static Set<String> holyDamage = new HashSet<>();
    private static Set<ItemStack> holyWeapons = new HashSet<>();
    private static Set<Enchantment> holyEnchantments =new HashSet<>();
    private static Set<String> holyMaterials = new HashSet<>();

    static {
        addHolyEnchantment(new ResourceLocation("minecraft", "smite"));
        addHolyDamage(DamageSource.OUT_OF_WORLD.getDamageType());
        addHolyDamage(DamageSource.CRAMMING.getDamageType());
        addHolyDamage(DamageSource.IN_WALL.getDamageType());
        addHolyDamage(DamageSourcesConsecration.HOLY.getDamageType());
        addHolyPotion(new ResourceLocation(Consecration.MODID, "smite_potion"));
        addHolyMaterial("silver");
    }

    public static void processIMC(FMLInterModComms.IMCEvent evt) {

        for (FMLInterModComms.IMCMessage message : evt.getMessages()) {

            String key = message.key;

            if (key.equalsIgnoreCase("holyWeapon") && message.isItemStackMessage()) {

                addHolyWeapon(message.getItemStackValue());
            }

            else if (key.equalsIgnoreCase("holyDamage") && message.isStringMessage()) {

                addHolyDamage(message.getStringValue());
            }

            else if (key.equalsIgnoreCase("holyPotion") && message.isResourceLocationMessage()) {

                addHolyPotion(message.getResourceLocationValue());
            }

            else if (key.equalsIgnoreCase("holyEntity") && message.isResourceLocationMessage()) {

                addHolyEntity(message.getResourceLocationValue());
            }
        }
    }

    public static void addUndead(ResourceLocation resource) {

        if (EntityList.isRegistered(resource)) {
            undeadList.add(resource);
        } else {
            Consecration.logger.log(Level.ERROR, "Tried to add undead entity that is not registered!" + " [" + resource.toString() + "]");
        }
    }

    public static void addHolyEntity(ResourceLocation resource) {

        if (EntityList.isRegistered(resource)) {
            holyEntities.add(resource);
        } else {
            Consecration.logger.log(Level.ERROR, "Tried to add holy entity that is not registered!" + " [" + resource.toString() + "]");
        }
    }

    public static void addHolyEnchantment(ResourceLocation resource) {

        if (Enchantment.REGISTRY.containsKey(resource)) {
            holyEnchantments.add(Enchantment.getEnchantmentByLocation(resource.toString()));
        } else {
            Consecration.logger.log(Level.ERROR, "Tried to add holy enchantment that is not registered!" + " [" + resource.toString() + "]");
        }
    }

    public static void addHolyMaterial(String name) {

        holyMaterials.add(name.toLowerCase());
    }

    public static void addHolyDamage(String damageSource) {
        holyDamage.add(damageSource);
    }

    public static void addHolyWeapon(ItemStack weapon) {
        holyWeapons.add(weapon);
    }

    public static void addHolyPotion(ResourceLocation resource) {

        Potion potion = Potion.getPotionFromResourceLocation(resource.toString());

        if (potion != null) {
            holyPotions.add(potion);
        } else {
            Consecration.logger.log(Level.ERROR, "Tried to add holy potion that is not registered!" + " [" + resource.toString() + "]");
        }
    }

    public static Set<Enchantment> getHolyEnchantments() { return holyEnchantments; }

    public static Set<Potion> getHolyPotions() {
        return holyPotions;
    }

    public static Set<String> getHolyDamage() {
        return holyDamage;
    }

    public static Set<String> getHolyMaterials() {
        return holyMaterials;
    }

    public static Set<ResourceLocation> getUndeadList() {
        return undeadList;
    }

    public static Set<ResourceLocation> getHolyEntities() { return holyEntities; }
}
