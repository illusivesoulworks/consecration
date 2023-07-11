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

package com.illusivesoulworks.consecration.common.registry;

import com.illusivesoulworks.consecration.ConsecrationConstants;
import com.illusivesoulworks.consecration.common.effect.HolyEffect;
import com.illusivesoulworks.consecration.common.enchantment.UndeadProtection;
import com.illusivesoulworks.consecration.common.entity.FireArrowEntity;
import com.illusivesoulworks.consecration.common.item.FireArrowItem;
import com.illusivesoulworks.consecration.common.item.FireStickItem;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

public class ConsecrationRegistry {

  public static final RegistryProvider<Enchantment> ENCHANTMENTS =
      RegistryProvider.get(Registries.ENCHANTMENT, ConsecrationConstants.MOD_ID);
  public static final RegistryProvider<Item> ITEMS =
      RegistryProvider.get(Registries.ITEM, ConsecrationConstants.MOD_ID);
  public static final RegistryProvider<MobEffect> EFFECTS =
      RegistryProvider.get(Registries.MOB_EFFECT, ConsecrationConstants.MOD_ID);
  public static final RegistryProvider<Potion> POTIONS =
      RegistryProvider.get(Registries.POTION, ConsecrationConstants.MOD_ID);
  public static final RegistryProvider<EntityType<?>> ENTITY_TYPES =
      RegistryProvider.get(Registries.ENTITY_TYPE, ConsecrationConstants.MOD_ID);

  public static final RegistryObject<MobEffect> HOLY_EFFECT =
      EFFECTS.register(ConsecrationConstants.Registry.HOLY, HolyEffect::new);

  public static final RegistryObject<Item> FIRE_ARROW =
      ITEMS.register(ConsecrationConstants.Registry.FIRE_ARROW, FireArrowItem::new);
  public static final RegistryObject<Item> FIRE_STICK =
      ITEMS.register(ConsecrationConstants.Registry.FIRE_STICK, FireStickItem::new);

  public static final RegistryObject<Enchantment> UNDEAD_PROTECTION =
      ENCHANTMENTS.register(ConsecrationConstants.Registry.UNDEAD_PROTECTION,
          UndeadProtection::new);

  public static final RegistryObject<Potion> HOLY_POTION =
      POTIONS.register(ConsecrationConstants.Registry.HOLY,
          () -> new Potion(ConsecrationConstants.Registry.HOLY,
              new MobEffectInstance(HOLY_EFFECT.get(), 1, 0)));
  public static final RegistryObject<Potion> STRONG_HOLY_POTION =
      POTIONS.register(ConsecrationConstants.Registry.STRONG_HOLY,
          () -> new Potion(ConsecrationConstants.Registry.HOLY,
              new MobEffectInstance(HOLY_EFFECT.get(), 1, 1)));

  public static final RegistryObject<EntityType<FireArrowEntity>> FIRE_ARROW_TYPE =
      ENTITY_TYPES.register(ConsecrationConstants.Registry.FIRE_ARROW,
          () -> EntityType.Builder.<FireArrowEntity>of(FireArrowEntity::new, MobCategory.MISC)
              .sized(0.5F, 0.3F).fireImmune().clientTrackingRange(64).updateInterval(5)
              .build(ConsecrationConstants.Registry.FIRE_ARROW));

  private static final ResourceKey<DamageType> HOLY_DAMAGE_KEY =
      ResourceKey.create(Registries.DAMAGE_TYPE,
          new ResourceLocation(ConsecrationConstants.MOD_ID, ConsecrationConstants.Registry.HOLY));

  public static Holder<DamageType> getHolyDamageType(RegistryAccess registryAccess) {
    return registryAccess.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(HOLY_DAMAGE_KEY);
  }

  public static void init() {
  }
}
