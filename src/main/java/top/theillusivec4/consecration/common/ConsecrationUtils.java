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
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.logging.log4j.Level;
import top.theillusivec4.consecration.Consecration;
import top.theillusivec4.consecration.api.ConsecrationApi;
import top.theillusivec4.consecration.api.ConsecrationApi.UndeadType;
import top.theillusivec4.consecration.common.ConsecrationConfig.PermissionMode;

public class ConsecrationUtils {

  private static final Field AOE_CLOUD_POTION = ObfuscationReflectionHelper
      .findField(AreaEffectCloudEntity.class, "field_184502_e");

  public static int protect(LivingEntity attacker, LivingEntity protect, DamageSource source) {

    if (ConsecrationUtils.getUndeadType(attacker.getType()) == UndeadType.ABSOLUTE) {
      return 0;
    }

    int[] level = new int[1];

    for (ItemStack stack : protect.getArmorInventoryList()) {

      if (!stack.isEmpty()) {
        Item item = stack.getItem();

        if (item instanceof ArmorItem) {
          IArmorMaterial material = ((ArmorItem) item).getArmorMaterial();
          for (ItemStack mat : material.getRepairMaterial().getMatchingStacks()) {
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

    if (!target.getType().isImmuneToFire() && source.isFireDamage()
        && undeadType != UndeadType.UNHOLY) {
      return DamageType.FIRE;
    }

    if (source.getImmediateSource() instanceof LivingEntity) {
      LivingEntity damager = (LivingEntity) source.getImmediateSource();
      ItemStack stack = damager.getHeldItemMainhand();
      Item item = stack.getItem();

      if (item instanceof TieredItem) {
        TieredItem tieredItem = (TieredItem) item;
        IItemTier tier = tieredItem.getTier();

        for (ItemStack mat : tier.getRepairMaterial().getMatchingStacks()) {
          ResourceLocation resourceLocation = mat.getItem().getRegistryName();

          if (resourceLocation != null && containsHolyMaterial(resourceLocation)) {
            return DamageType.HOLY;
          }
        }
      } else if (isHolyItem(item)) {
        return DamageType.HOLY;
      }

      if (hasHolyEnchantment(stack)) {
        return DamageType.HOLY;
      }
    }

    if (isHolyDamage(source) || isHolyEntity(source.getImmediateSource()) || isHolyPotion(
        source.getImmediateSource())) {
      return DamageType.HOLY;
    }
    return processHolyFunctions(target, source);
  }

  public static boolean isUndying(final LivingEntity livingEntity) {
    return isValidCreature(livingEntity) && isValidDimension(
        livingEntity.getEntityWorld().func_234923_W_().func_240901_a_());
  }

  public static boolean isValidCreature(final LivingEntity livingEntity) {
    return (ConsecrationConfig.defaultUndead
        && livingEntity.getCreatureAttribute() == CreatureAttribute.UNDEAD) || ConsecrationApi
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

  public static boolean isHolyEffect(Effect effect) {
    return ConsecrationApi.getHolyRegistry().getHolyEffects().contains(effect);
  }

  public static boolean isHolyPotion(Entity entity) {
    List<EffectInstance> effects = Lists.newArrayList();

    if (entity instanceof PotionEntity) {
      effects.addAll(PotionUtils.getEffectsFromStack(((PotionEntity) entity).getItem()));
    } else if (entity instanceof AreaEffectCloudEntity) {
      Potion potion = null;
      try {
        potion = (Potion) AOE_CLOUD_POTION.get(entity);
      } catch (IllegalAccessException e) {
        Consecration.LOGGER.log(Level.ERROR, "Error getting potion from AoE cloud " + entity);
      }
      if (potion != null) {
        effects.addAll(potion.getEffects());
      }
    }
    Set<Effect> holyEffects = ConsecrationApi.getHolyRegistry().getHolyEffects();

    for (EffectInstance effect : effects) {
      Effect potion = effect.getPotion();

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
    return ConsecrationApi.getHolyRegistry().getHolyDamage().contains(source.getDamageType());
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
