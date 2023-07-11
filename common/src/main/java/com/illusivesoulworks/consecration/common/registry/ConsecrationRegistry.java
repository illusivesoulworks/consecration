package com.illusivesoulworks.consecration.common.registry;

import com.illusivesoulworks.consecration.ConsecrationConstants;
import com.illusivesoulworks.consecration.common.effect.HolyEffect;
import com.illusivesoulworks.consecration.common.enchantment.UndeadProtection;
import com.illusivesoulworks.consecration.common.entity.FireArrowEntity;
import com.illusivesoulworks.consecration.common.item.FireArrowItem;
import com.illusivesoulworks.consecration.common.item.FireStickItem;
import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;

public class ConsecrationRegistry {

  public static final RegistryProvider<Enchantment> ENCHANTMENTS =
      RegistryProvider.get(Registry.ENCHANTMENT, ConsecrationConstants.MOD_ID);
  public static final RegistryProvider<Item> ITEMS =
      RegistryProvider.get(Registry.ITEM, ConsecrationConstants.MOD_ID);
  public static final RegistryProvider<MobEffect> EFFECTS =
      RegistryProvider.get(Registry.MOB_EFFECT, ConsecrationConstants.MOD_ID);
  public static final RegistryProvider<Potion> POTIONS =
      RegistryProvider.get(Registry.POTION, ConsecrationConstants.MOD_ID);
  public static final RegistryProvider<EntityType<?>> ENTITY_TYPES =
      RegistryProvider.get(Registry.ENTITY_TYPE, ConsecrationConstants.MOD_ID);

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

  public static void init() {
  }
}
