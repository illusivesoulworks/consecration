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

import com.illusivesoulworks.consecration.ConsecrationConstants;
import com.illusivesoulworks.consecration.api.ConsecrationApi;
import com.illusivesoulworks.consecration.api.IUndying;
import com.illusivesoulworks.consecration.api.UndeadType;
import com.illusivesoulworks.consecration.api.VulnerabilityType;
import com.illusivesoulworks.consecration.platform.Services;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class ConsecrationApiImpl extends ConsecrationApi {

  public static final ConsecrationApi INSTANCE = new ConsecrationApiImpl();

  @Override
  public String getModId() {
    return ConsecrationConstants.MOD_ID;
  }

  @Override
  public String getHolyIdentifier() {
    return ConsecrationConstants.Registry.HOLY;
  }

  @Override
  public Optional<? extends IUndying> getUndying(LivingEntity livingEntity) {

    if (!UndeadTypes.isUndying(livingEntity)) {
      return Optional.empty();
    }
    return Services.CAPABILITY.getUndying(livingEntity);
  }

  @Override
  public boolean isHolyEntity(Entity entity) {
    return HolySources.contains(entity);
  }

  @Override
  public boolean isHolyItem(Item item) {
    return HolySources.contains(item);
  }

  @Override
  public boolean isHolyItem(ItemStack stack) {
    return isHolyItem(stack.getItem()) || Services.REGISTRY.canSmite(stack);
  }

  @Override
  public boolean isHolyEnchantment(Enchantment enchantment) {
    return HolySources.contains(enchantment);
  }

  @Override
  public boolean isHolyEffect(MobEffect mobEffect) {
    return HolySources.contains(mobEffect);
  }

  @Override
  public boolean isHolyDamage(String damageType) {
    return HolySources.containsDamage(damageType);
  }

  @Override
  public boolean isHolyMaterial(String material) {
    return HolySources.containsMaterial(material);
  }

  @Override
  public int getHolyProtectionLevel(LivingEntity attacker,
                                    LivingEntity livingEntity, DamageSource damageSource) {
    return HolySources.getHolyProtectionLevel(attacker, livingEntity, damageSource);
  }

  @Override
  public boolean isHolyAttack(LivingEntity livingEntity, DamageSource damageSource) {
    return HolySources.isHolyAttack(livingEntity, damageSource);
  }

  @Override
  public DamageSource causeHolyDamage(@Nonnull Entity entity) {
    return new EntityDamageSource(ConsecrationConstants.Registry.HOLY, entity).setMagic();
  }

  @Override
  public DamageSource causeIndirectHolyDamage(@Nonnull Entity source, @Nullable Entity indirect) {
    return new IndirectEntityDamageSource(ConsecrationConstants.Registry.HOLY, source, indirect)
        .setMagic();
  }

  @Override
  public DamageSource causeHolyDamage() {
    return Services.REGISTRY.getDamageSource("holy").setMagic();
  }

  @Override
  public VulnerabilityType getVulnerabilityType(LivingEntity livingEntity, DamageSource source) {
    UndeadType undeadType = UndeadTypes.getType(livingEntity);
    ConsecrationApi api = ConsecrationApi.getInstance();

    if (undeadType == UndeadType.RESISTANT) {
      return VulnerabilityType.NONE;
    }

    if (!livingEntity.getType().fireImmune() && source.isFire() &&
        undeadType != UndeadType.FIRE_RESISTANT) {
      return VulnerabilityType.FIRE;
    }

    if (source.getDirectEntity() instanceof LivingEntity damager) {
      ItemStack stack = damager.getMainHandItem();
      Item item = stack.getItem();

      if (item instanceof TieredItem tieredItem) {
        Tier tier = tieredItem.getTier();

        for (ItemStack mat : tier.getRepairIngredient().getItems()) {
          ResourceLocation resourceLocation = Services.REGISTRY.getKey(mat.getItem());

          if (resourceLocation != null && (api.isHolyMaterial(resourceLocation.toString()) ||
              api.isHolyMaterial(resourceLocation.getPath()))) {
            return VulnerabilityType.HOLY;
          }
        }
      }

      if (api.isHolyItem(stack)) {
        return VulnerabilityType.HOLY;
      }

      for (Enchantment enchantment : EnchantmentHelper.getEnchantments(stack).keySet()) {

        if (api.isHolyEnchantment(enchantment)) {
          return VulnerabilityType.HOLY;
        }
      }
    }

    if (api.isHolyDamage(source.getMsgId()) ||
        api.isHolyEntity(source.getDirectEntity()) ||
        api.isHolyAttack(livingEntity, source)) {
      return VulnerabilityType.HOLY;
    }
    return VulnerabilityType.NONE;
  }

  @Override
  public UndeadType getUndeadType(LivingEntity livingEntity) {
    return UndeadTypes.getType(livingEntity);
  }
}
