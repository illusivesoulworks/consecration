/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.util;

import c4.consecration.Consecration;
import c4.consecration.common.init.ConsecrationDamageSources;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import org.apache.logging.log4j.Level;
import scala.collection.immutable.Stream;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UndeadRegistry {

    private static List<ResourceLocation> undeadList = new ArrayList<>();
    private static List<ResourceLocation> holyEntities = new ArrayList<>();
    private static List<Potion> holyPotions = new ArrayList<>();
    private static List<ItemStack> holyWeapons = new ArrayList<>();
    private static List<Enchantment> holyEnchantments = new ArrayList<>();
    private static Set<String> holyDamage = new HashSet<>();
    private static Set<String> holyMaterials = new HashSet<>();

    public static void processIMC(FMLInterModComms.IMCEvent evt) {

        for (FMLInterModComms.IMCMessage message : evt.getMessages()) {

            String key = message.key;

            if (message.isItemStackMessage()) {
                if (equalsKey(key, "holyweapon")) {
                    addHolyWeapon(message.getItemStackValue());
                }
            } else if (message.isResourceLocationMessage()) {
                if (equalsKey(key, "holypotion")) {
                    addHolyPotion(message.getResourceLocationValue());
                } else if (equalsKey(key, "holyentity")) {
                    addHolyEntity(message.getResourceLocationValue());
                } else if (equalsKey(key, "holyenchantment")) {
                    addHolyEnchantment(message.getResourceLocationValue());
                }
            } else if (message.isStringMessage()) {
                if (equalsKey(key, "holydamage")) {
                    addHolyDamage(message.getStringValue());
                } else if (equalsKey(key, "holymaterial")) {
                    addHolyMaterial(message.getStringValue());
                }
            }
        }
    }

    private static boolean equalsKey(String key, String value) {
        return key.equalsIgnoreCase(value);
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

    public static void addHolyWeapon(String weapon) {
        Item item = Item.getByNameOrId(weapon);
        if (item != null) {
            addHolyWeapon(new ItemStack(item));
        } else {
            Consecration.logger.log(Level.ERROR, "Tried to add holy weapon that is not registered!" + " [" + weapon
                    + "]");
        }
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

    public static ImmutableList<Enchantment> getHolyEnchantments() {
        return ImmutableList.copyOf(holyEnchantments); }

    public static ImmutableList<Potion> getHolyPotions() {
        return ImmutableList.copyOf(holyPotions);
    }

    public static ImmutableSet<String> getHolyDamage() {
        return ImmutableSet.copyOf(holyDamage);
    }

    public static ImmutableList<ItemStack> getHolyWeapons() {
        return ImmutableList.copyOf(holyWeapons); }

    public static ImmutableSet<String> getHolyMaterials() {
        return ImmutableSet.copyOf(holyMaterials);
    }

    public static ImmutableList<ResourceLocation> getUndeadList() {
        return ImmutableList.copyOf(undeadList);
    }

    public static ImmutableList<ResourceLocation> getHolyEntities() {
        return ImmutableList.copyOf(holyEntities); }
}
