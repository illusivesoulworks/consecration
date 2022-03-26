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

package top.theillusivec4.consecration.common.registry;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.consecration.api.ConsecrationApi;
import top.theillusivec4.consecration.common.enchantment.UndeadProtection;
import top.theillusivec4.consecration.common.entity.FireArrowEntity;
import top.theillusivec4.consecration.common.item.FireArrowItem;
import top.theillusivec4.consecration.common.item.FireStickItem;
import top.theillusivec4.consecration.common.effect.HolyEffect;

public class ConsecrationRegistry {

  private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(
      ForgeRegistries.MOB_EFFECTS, ConsecrationApi.MOD_ID);
  private static final DeferredRegister<Item> ITEMS =
      DeferredRegister.create(ForgeRegistries.ITEMS, ConsecrationApi.MOD_ID);
  private static final DeferredRegister<Enchantment> ENCHANTMENTS =
      DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, ConsecrationApi.MOD_ID);
  private static final DeferredRegister<Potion> POTIONS =
      DeferredRegister.create(ForgeRegistries.POTIONS, ConsecrationApi.MOD_ID);
  private static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
      DeferredRegister.create(ForgeRegistries.ENTITIES, ConsecrationApi.MOD_ID);

  public static final RegistryObject<MobEffect> HOLY_EFFECT =
      EFFECTS.register(RegistryReference.HOLY,
          HolyEffect::new);

  public static final RegistryObject<Item> FIRE_ARROW =
      ITEMS.register(RegistryReference.FIRE_ARROW, FireArrowItem::new);
  public static final RegistryObject<Item> FIRE_STICK =
      ITEMS.register(RegistryReference.FIRE_STICK, FireStickItem::new);

  public static final RegistryObject<Enchantment> UNDEAD_PROTECTION =
      ENCHANTMENTS.register(RegistryReference.UNDEAD_PROTECTION, UndeadProtection::new);

  public static final RegistryObject<Potion> HOLY_POTION =
      POTIONS.register(RegistryReference.HOLY, () -> new Potion(ConsecrationApi.HOLY_IDENTIFIER,
          new MobEffectInstance(ConsecrationRegistry.HOLY_EFFECT.get(), 1, 0)));
  public static final RegistryObject<Potion> STRONG_HOLY_POTION =
      POTIONS.register(RegistryReference.STRONG_HOLY, () -> new Potion(ConsecrationApi.HOLY_IDENTIFIER,
          new MobEffectInstance(ConsecrationRegistry.HOLY_EFFECT.get(), 1, 1)));

  public static final RegistryObject<EntityType<FireArrowEntity>> FIRE_ARROW_TYPE =
      ENTITY_TYPES.register(RegistryReference.FIRE_ARROW,
          () -> EntityType.Builder.<FireArrowEntity>of(FireArrowEntity::new,
                  MobCategory.MISC).sized(0.5F, 0.3F).fireImmune().setTrackingRange(64)
              .setUpdateInterval(5).setShouldReceiveVelocityUpdates(true)
              .build(RegistryReference.FIRE_ARROW));

  public static void setup(IEventBus eventBus) {
    EFFECTS.register(eventBus);
    ITEMS.register(eventBus);
    POTIONS.register(eventBus);
    ENCHANTMENTS.register(eventBus);
    ENTITY_TYPES.register(eventBus);
  }
}
