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
import top.theillusivec4.consecration.api.ConsecrationAPI;
import top.theillusivec4.consecration.api.ConsecrationAPI.UndeadType;
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

    for (BiFunction<LivingEntity, DamageSource, Boolean> func : ConsecrationAPI
        .getHolyProtection()) {
      if (func.apply(protect, source)) {
        level[0]++;
        break;
      }
    }
    return level[0];
  }

  public static DamageType smite(LivingEntity target, DamageSource source) {
    UndeadType undeadType = getUndeadType(target.getType());

    if (undeadType == UndeadType.ABSOLUTE) {
      return DamageType.NONE;
    }

    if (!target.isImmuneToFire() && source.isFireDamage() && undeadType != UndeadType.UNHOLY) {
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
        livingEntity.getEntityWorld().dimension.getType().getId());
  }

  public static boolean isValidCreature(final LivingEntity livingEntity) {
    return (ConsecrationConfig.SERVER.defaultUndead.get()
        && livingEntity.getCreatureAttribute() == CreatureAttribute.UNDEAD) || ConsecrationAPI
        .getUndead().containsKey(livingEntity.getType());
  }

  public static boolean isValidDimension(final int id) {
    List<? extends Integer> dimensions = ConsecrationConfig.SERVER.dimensions.get();
    PermissionMode permissionMode = ConsecrationConfig.SERVER.dimensionPermission.get();

    if (dimensions.isEmpty()) {
      return true;
    } else if (permissionMode == PermissionMode.BLACKLIST) {
      return !dimensions.contains(id);
    } else {
      return dimensions.contains(id);
    }
  }

  public static boolean containsUndead(EntityType<?> entityType) {
    return ConsecrationAPI.getUndead().containsKey(entityType);
  }

  public static UndeadType getUndeadType(EntityType<?> entityType) {
    return ConsecrationAPI.getUndead().getOrDefault(entityType, UndeadType.NORMAL);
  }

  public static boolean isHolyEffect(Effect effect) {
    return ConsecrationAPI.getHolyEffects().contains(effect);
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

    for (EffectInstance effect : effects) {
      Effect potion = effect.getPotion();

      if (ConsecrationAPI.getHolyEffects().contains(potion)) {
        return true;
      }
    }
    return false;
  }

  public static boolean hasHolyEnchantment(ItemStack stack) {

    for (Enchantment enchantment : EnchantmentHelper.getEnchantments(stack).keySet()) {

      if (ConsecrationAPI.getHolyEnchantments().contains(enchantment)) {
        return true;
      }
    }
    return false;
  }

  public static DamageType processHolyFunctions(LivingEntity target, DamageSource source) {

    for (BiFunction<LivingEntity, DamageSource, Boolean> func : ConsecrationAPI.getHolyAttacks()) {

      if (func.apply(target, source)) {
        return DamageType.HOLY;
      }
    }
    return DamageType.NONE;
  }

  public static boolean isHolyEntity(@Nullable Entity entity) {
    return entity != null && ConsecrationAPI.getHolyEntities().contains(entity.getType());
  }

  public static boolean isHolyDamage(DamageSource source) {
    return ConsecrationAPI.getHolyDamage().contains(source.getDamageType());
  }

  public static boolean isHolyItem(Item item) {
    return ConsecrationAPI.getHolyItems().contains(item);
  }

  public static boolean containsHolyMaterial(ResourceLocation resourceLocation) {

    for (String mat : ConsecrationAPI.getHolyMaterials()) {
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
