/*
 * Copyright (C) 2017-2023 Illusive Soulworks
 *
 * Consecration is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Consecration is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Consecration.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.consecration.common.impl;

import com.illusivesoulworks.consecration.api.ConsecrationApi;
import com.illusivesoulworks.consecration.api.UndeadType;
import com.illusivesoulworks.consecration.common.config.ConsecrationConfig;
import com.illusivesoulworks.consecration.platform.Services;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;

public class HolySources {

  private static final Queue<BiFunction<LivingEntity, DamageSource, Boolean>> HOLY_ATTACKS =
      new ConcurrentLinkedQueue<>();
  private static final Queue<BiFunction<LivingEntity, DamageSource, Integer>> HOLY_PROTECTION =
      new ConcurrentLinkedQueue<>();
  private static final Set<String> HOLY_DAMAGE = new HashSet<>();
  private static final Set<String> HOLY_MATERIALS = new HashSet<>();

  public static void reloadConfigs() {
    HOLY_DAMAGE.clear();
    HOLY_DAMAGE.addAll(ConsecrationConfig.CONFIG.holyDamage.get());
    HOLY_MATERIALS.clear();
    HOLY_MATERIALS.addAll(ConsecrationConfig.CONFIG.holyMaterials.get());
  }

  public static void addHolyAttack(BiFunction<LivingEntity, DamageSource, Boolean> func) {
    HOLY_ATTACKS.add(func);
  }

  public static void addHolyProtection(BiFunction<LivingEntity, DamageSource, Integer> func) {
    HOLY_PROTECTION.add(func);
  }

  public static boolean contains(Item item) {
    return Services.REGISTRY.isHolyTag(item);
  }

  public static boolean contains(MobEffect effect) {
    return Services.REGISTRY.isHolyTag(effect);
  }

  public static boolean contains(Enchantment enchantment) {
    return Services.REGISTRY.isHolyTag(enchantment);
  }

  public static boolean contains(Entity entity) {

    if (entity == null) {
      return false;
    }
    List<MobEffectInstance> effects = new ArrayList<>();

    if (entity instanceof ThrownPotion) {
      effects.addAll(PotionUtils.getMobEffects(((ThrownPotion) entity).getItem()));
    } else if (entity instanceof AreaEffectCloud cloud) {
      effects.addAll(cloud.getPotion().getEffects());
    }

    for (MobEffectInstance effect : effects) {

      if (contains(effect.getEffect())) {
        return true;
      }
    }
    return Services.REGISTRY.isHolyTag(entity.getType());
  }

  public static int getHolyProtectionLevel(LivingEntity attacker, LivingEntity defender,
                                           DamageSource damageSource) {

    if (UndeadTypes.getType(attacker) == UndeadType.RESISTANT) {
      return 0;
    }
    int level = 0;

    for (ItemStack stack : defender.getArmorSlots()) {

      if (!stack.isEmpty()) {
        Item item = stack.getItem();

        if (item instanceof ArmorItem) {
          ArmorMaterial material = ((ArmorItem) item).getMaterial();
          Ingredient ing = material.getRepairIngredient();
          // Some mods are returning null here, so we have to check for it
          if (ing != null) {

            for (ItemStack mat : ing.getItems()) {
              ResourceLocation resourceLocation = Services.REGISTRY.getKey(mat.getItem());

              if (resourceLocation != null && (ConsecrationApi.getInstance()
                  .isHolyMaterial(resourceLocation.toString()) || ConsecrationApi.getInstance()
                  .isHolyMaterial(resourceLocation.getPath()))) {
                level++;
              }
            }
          }
        } else if (ConsecrationApi.getInstance().isHolyItem(stack)) {
          level++;
        }
      }
    }

    for (BiFunction<LivingEntity, DamageSource, Integer> func : HOLY_PROTECTION) {
      level += func.apply(defender, damageSource);
    }
    return level;
  }

  public static boolean isHolyAttack(LivingEntity livingEntity, DamageSource damageSource) {

    for (BiFunction<LivingEntity, DamageSource, Boolean> func : HOLY_ATTACKS) {

      if (func.apply(livingEntity, damageSource)) {
        return true;
      }
    }
    return false;
  }

  public static boolean containsMaterial(String material) {

    for (String holyMaterial : HOLY_MATERIALS) {
      String pattern = "(^|\\w*[_-])" + holyMaterial + "(\\b|[_-]\\w*)";

      if (material.matches(pattern)) {
        return true;
      }
    }
    return false;
  }

  public static boolean containsDamage(String damage) {
    return HOLY_DAMAGE.contains(damage);
  }
}
