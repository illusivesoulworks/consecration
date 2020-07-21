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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.InterModComms.IMCMessage;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.consecration.api.ConsecrationApi;
import top.theillusivec4.consecration.api.ConsecrationApi.IMC;
import top.theillusivec4.consecration.api.ConsecrationApi.UndeadType;
import top.theillusivec4.consecration.api.IHolyRegistry;

public class ConsecrationSeed {

  private static Map<EntityType<?>, UndeadType> undeadMapImc = new HashMap<>();
  private static List<BiFunction<LivingEntity, DamageSource, Boolean>> holyAttacksImc = new ArrayList<>();
  private static List<BiFunction<LivingEntity, DamageSource, Integer>> holyProtectionImc = new ArrayList<>();
  private static Set<EntityType<?>> holyEntitiesImc = new HashSet<>();
  private static Set<Effect> holyEffectsImc = new HashSet<>();
  private static Set<Item> holyItemsImc = new HashSet<>();
  private static Set<Enchantment> holyEnchantmentsImc = new HashSet<>();
  private static Set<String> holyDamageImc = new HashSet<>();
  private static Set<String> holyMaterialsImc = new HashSet<>();

  private static Map<EntityType<?>, UndeadType> undeadMap = new HashMap<>();
  private static Set<EntityType<?>> holyEntities = new HashSet<>();
  private static Set<Effect> holyEffects = new HashSet<>();
  private static Set<Item> holyItems = new HashSet<>();
  private static Set<Enchantment> holyEnchantments = new HashSet<>();
  private static Set<String> holyDamage = new HashSet<>();
  private static Set<String> holyMaterials = new HashSet<>();

  public static void fillRegistry() {
    registerConfig();
    IHolyRegistry registry = ConsecrationApi.getHolyRegistry();
    registry.clear();

    undeadMapImc.forEach((registry::addUndead));
    holyAttacksImc.forEach(registry::addHolyAttack);
    holyProtectionImc.forEach(registry::addHolyProtection);
    holyEnchantmentsImc.forEach(registry::addHolyEnchantment);
    holyEntitiesImc.forEach(registry::addHolyEntity);
    holyEffectsImc.forEach(registry::addHolyEffect);
    holyItemsImc.forEach(registry::addHolyItem);
    holyDamageImc.forEach(registry::addHolyDamage);
    holyMaterialsImc.forEach(registry::addHolyMaterial);

    undeadMap.forEach((registry::addUndead));
    holyEnchantments.forEach(registry::addHolyEnchantment);
    holyEntities.forEach(registry::addHolyEntity);
    holyEffects.forEach(registry::addHolyEffect);
    holyItems.forEach(registry::addHolyItem);
    holyDamage.forEach(registry::addHolyDamage);
    holyMaterials.forEach(registry::addHolyMaterial);
  }

  @SuppressWarnings("unchecked")
  public static void registerImc(Stream<IMCMessage> imcStream) {

    if (imcStream != null) {
      imcStream.forEach(imcMessage -> {
        Object message = imcMessage.getMessageSupplier().get();
        String method = imcMessage.getMethod();

        if (message instanceof String) {
          String content = (String) message;

          switch (method) {
            case IMC.UNDEAD:
              ConsecrationParser.getUndeadType(content)
                  .ifPresent(tuple -> undeadMapImc.putIfAbsent(tuple.getA(), tuple.getB()));
              break;
            case IMC.HOLY_ENTITY:
              EntityType.byKey(content).ifPresent(type -> holyEntitiesImc.add(type));
              break;
            case IMC.HOLY_EFFECT:
              Effect effect = ForgeRegistries.POTIONS.getValue(new ResourceLocation(content));

              if (effect != null) {
                holyEffectsImc.add(effect);
              }
              break;
            case IMC.HOLY_ITEM:
              Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(content));

              if (item != null) {
                holyItemsImc.add(item);
              }
              break;
            case IMC.HOLY_ENCHANTMENT:
              Enchantment enchantment = ForgeRegistries.ENCHANTMENTS
                  .getValue(new ResourceLocation(content));

              if (enchantment != null) {
                holyEnchantmentsImc.add(enchantment);
              }
              break;
            case IMC.HOLY_MATERIAL:
              holyMaterialsImc.add(content);
              break;
            case IMC.HOLY_DAMAGE:
              holyDamageImc.add(content);
              break;
          }
        } else if (message instanceof BiFunction) {

          switch (method) {
            case IMC.HOLY_ATTACK:
              holyAttacksImc.add((BiFunction<LivingEntity, DamageSource, Boolean>) message);
              break;
            case IMC.HOLY_PROTECTION:
              holyProtectionImc.add((BiFunction<LivingEntity, DamageSource, Integer>) message);
              break;
          }
        }
      });
    }
  }

  public static void registerConfig() {
    undeadMap.clear();
    holyEntities.clear();
    holyEffects.clear();
    holyItems.clear();
    holyEnchantments.clear();
    holyDamage.clear();
    holyMaterials.clear();

    ConsecrationConfig.undeadList.forEach(undead -> ConsecrationParser.getUndeadType(undead)
        .ifPresent(tuple -> undeadMap.putIfAbsent(tuple.getA(), tuple.getB())));
    ConsecrationConfig.holyEntities
        .forEach(entity -> EntityType.byKey(entity).ifPresent(type -> holyEntities.add(type)));
    ConsecrationConfig.holyEffects.forEach(effect -> {
      Effect type = ForgeRegistries.POTIONS.getValue(new ResourceLocation(effect));

      if (type != null) {
        holyEffects.add(type);
      }
    });
    ConsecrationConfig.holyItems.forEach(item -> {
      Item type = ForgeRegistries.ITEMS.getValue(new ResourceLocation(item));

      if (type != null) {
        holyItems.add(type);
      }
    });
    ConsecrationConfig.holyEnchantments.forEach(enchantment -> {
      Enchantment type = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantment));

      if (type != null) {
        holyEnchantments.add(type);
      }
    });
    holyDamage.addAll(ConsecrationConfig.holyDamage);
    holyMaterials.addAll(ConsecrationConfig.holyMaterials);
  }
}
