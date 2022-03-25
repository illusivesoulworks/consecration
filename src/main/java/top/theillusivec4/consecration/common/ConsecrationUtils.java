/*
 * Copyright (c) 2017-2020 C4
 *
 * This file is part of Consecration, a mod made for Minecraft.
 *
 * Consecration is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Consecration is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Consecration.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.consecration.common;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import top.theillusivec4.consecration.api.ConsecrationApi;
import top.theillusivec4.consecration.api.ConsecrationApi.UndeadType;
import top.theillusivec4.consecration.common.ConsecrationConfig.PermissionMode;

public class ConsecrationUtils {

  public static int protect(LivingEntity attacker, LivingEntity protect, DamageSource source) {

    if (ConsecrationUtils.getUndeadType(attacker.getType()) == UndeadType.ABSOLUTE) {
      return 0;
    }

    int[] level = new int[1];

    for (ItemStack stack : protect.getArmorSlots()) {

      if (!stack.isEmpty()) {
        Item item = stack.getItem();

        if (item instanceof ArmorItem) {
          ArmorMaterial material = ((ArmorItem) item).getMaterial();
          for (ItemStack mat : material.getRepairIngredient().getItems()) {
            ResourceLocation resourceLocation = mat.getItem().getRegistryName();

            if (resourceLocation != null && containsHolyMaterial(resourceLocation)) {
              level[0]++;
            }
          }
        } else if (isHolyItem(item)) {
          level[0]++;
        }
      }
    }

    for (BiFunction<LivingEntity, DamageSource, Integer> func : ConsecrationApi.getHolyRegistry()
        .getHolyProtection()) {
      level[0] += func.apply(protect, source);
    }
    return level[0];
  }

  public static DamageType smite(LivingEntity target, DamageSource source) {
    UndeadType undeadType = getUndeadType(target.getType());

    if (undeadType == UndeadType.ABSOLUTE) {
      return DamageType.NONE;
    }

    if (!target.getType().fireImmune() && source.isFire()
        && undeadType != UndeadType.UNHOLY) {
      return DamageType.FIRE;
    }

    if (source.getDirectEntity() instanceof LivingEntity damager) {
      ItemStack stack = damager.getMainHandItem();
      Item item = stack.getItem();

      if (item instanceof TieredItem tieredItem) {
        Tier tier = tieredItem.getTier();

        for (ItemStack mat : tier.getRepairIngredient().getItems()) {
          ResourceLocation resourceLocation = mat.getItem().getRegistryName();

          if (resourceLocation != null && containsHolyMaterial(resourceLocation)) {
            return DamageType.HOLY;
          }
        }
      }

      if (isHolyItem(item) || hasHolyEnchantment(stack)) {
        return DamageType.HOLY;
      }
    }

    if (isHolyDamage(source) || isHolyEntity(source.getDirectEntity()) || isHolyPotion(
        source.getDirectEntity())) {
      return DamageType.HOLY;
    }
    return processHolyFunctions(target, source);
  }

  public static boolean isUndying(final LivingEntity livingEntity) {
    return isValidCreature(livingEntity) && isValidDimension(
        livingEntity.getCommandSenderWorld().dimension().location());
  }

  public static boolean isValidCreature(final LivingEntity livingEntity) {
    return (ConsecrationConfig.defaultUndead
        && livingEntity.getMobType() == MobType.UNDEAD) || ConsecrationApi
        .getHolyRegistry().getUndead().containsKey(livingEntity.getType());
  }

  public static boolean isValidDimension(final ResourceLocation resourceLocation) {
    Set<ResourceLocation> dimensions = ConsecrationConfig.dimensions;
    PermissionMode permissionMode = ConsecrationConfig.dimensionPermission;

    if (dimensions.isEmpty()) {
      return true;
    } else if (permissionMode == PermissionMode.BLACKLIST) {
      return !dimensions.contains(resourceLocation);
    } else {
      return dimensions.contains(resourceLocation);
    }
  }

  public static boolean containsUndead(EntityType<?> entityType) {
    return ConsecrationApi.getHolyRegistry().getUndead().containsKey(entityType);
  }

  public static UndeadType getUndeadType(EntityType<?> entityType) {
    return ConsecrationApi.getHolyRegistry().getUndead()
        .getOrDefault(entityType, UndeadType.NORMAL);
  }

  public static boolean isHolyEffect(MobEffect effect) {
    return ConsecrationApi.getHolyRegistry().getHolyEffects().contains(effect);
  }

  public static boolean isHolyPotion(Entity entity) {
    List<MobEffectInstance> effects = Lists.newArrayList();

    if (entity instanceof ThrownPotion) {
      effects.addAll(PotionUtils.getMobEffects(((ThrownPotion) entity).getItem()));
    } else if (entity instanceof AreaEffectCloud cloud) {
      effects.addAll(cloud.getPotion().getEffects());
    }
    Set<MobEffect> holyEffects = ConsecrationApi.getHolyRegistry().getHolyEffects();

    for (MobEffectInstance effect : effects) {
      MobEffect potion = effect.getEffect();

      if (holyEffects.contains(potion)) {
        return true;
      }
    }
    return false;
  }

  public static boolean hasHolyEnchantment(ItemStack stack) {
    Set<Enchantment> enchantments = ConsecrationApi.getHolyRegistry().getHolyEnchantments();

    for (Enchantment enchantment : EnchantmentHelper.getEnchantments(stack).keySet()) {

      if (enchantments.contains(enchantment)) {
        return true;
      }
    }
    return false;
  }

  public static DamageType processHolyFunctions(LivingEntity target, DamageSource source) {

    for (BiFunction<LivingEntity, DamageSource, Boolean> func : ConsecrationApi.getHolyRegistry()
        .getHolyAttacks()) {

      if (func.apply(target, source)) {
        return DamageType.HOLY;
      }
    }
    return DamageType.NONE;
  }

  public static boolean isHolyEntity(@Nullable Entity entity) {
    return entity != null && ConsecrationApi.getHolyRegistry().getHolyEntities()
        .contains(entity.getType());
  }

  public static boolean isHolyDamage(DamageSource source) {
    return ConsecrationApi.getHolyRegistry().getHolyDamage().contains(source.getMsgId());
  }

  public static boolean isHolyItem(Item item) {
    return ConsecrationApi.getHolyRegistry().getHolyItems().contains(item);
  }

  public static boolean containsHolyMaterial(ResourceLocation resourceLocation) {

    for (String mat : ConsecrationApi.getHolyRegistry().getHolyMaterials()) {
      String pattern = "^" + mat + "(\\b|[_-]\\w*)";

      if (resourceLocation.getPath().matches(pattern)) {
        return true;
      }
    }
    return false;
  }

  public enum DamageType {
    NONE, FIRE, HOLY
  }
}
