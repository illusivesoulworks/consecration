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

package com.illusivesoulworks.consecration.platform;

import com.illusivesoulworks.consecration.ConsecrationConstants;
import com.illusivesoulworks.consecration.api.UndeadType;
import com.illusivesoulworks.consecration.common.registry.RegistryObject;
import com.illusivesoulworks.consecration.common.registry.RegistryProvider;
import com.illusivesoulworks.consecration.platform.services.IRegistryService;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class FabricRegistryService implements IRegistryService {

  public static final TagKey<EntityType<?>> UNDEAD = TagKey.create(Registries.ENTITY_TYPE,
      new ResourceLocation(ConsecrationConstants.MOD_ID, "undead"));
  public static final TagKey<EntityType<?>> FIRE_RESISTANT =
      TagKey.create(Registries.ENTITY_TYPE,
          new ResourceLocation(ConsecrationConstants.MOD_ID, "fire_resistant"));
  public static final TagKey<EntityType<?>> HOLY_RESISTANT =
      TagKey.create(Registries.ENTITY_TYPE,
          new ResourceLocation(ConsecrationConstants.MOD_ID, "holy_resistant"));
  public static final TagKey<EntityType<?>> RESISTANT = TagKey.create(Registries.ENTITY_TYPE,
      new ResourceLocation(ConsecrationConstants.MOD_ID, "resistant"));

  public static final TagKey<Item> HOLY_ITEMS = TagKey.create(Registries.ITEM,
      new ResourceLocation(ConsecrationConstants.MOD_ID, ConsecrationConstants.Registry.HOLY));
  public static final TagKey<MobEffect> HOLY_EFFECTS = TagKey.create(Registries.MOB_EFFECT,
      new ResourceLocation(ConsecrationConstants.MOD_ID, ConsecrationConstants.Registry.HOLY));
  public static final TagKey<Enchantment> HOLY_ENCHANTMENTS = TagKey.create(Registries.ENCHANTMENT,
      new ResourceLocation(ConsecrationConstants.MOD_ID, ConsecrationConstants.Registry.HOLY));
  public static final TagKey<EntityType<?>> HOLY_ENTITIES = TagKey.create(Registries.ENTITY_TYPE,
      new ResourceLocation(ConsecrationConstants.MOD_ID, ConsecrationConstants.Registry.HOLY));

  @Override
  public void processUndeadTypes(BiConsumer<EntityType<?>, UndeadType> biConsumer) {

    for (EntityType<?> entity : BuiltInRegistries.ENTITY_TYPE.stream().toList()) {
      UndeadType type = UndeadType.NOT;

      if (entity.is(UNDEAD)) {
        type = UndeadType.DEFAULT;
      } else if (entity.is(FIRE_RESISTANT)) {
        type = UndeadType.FIRE_RESISTANT;
      } else if (entity.is(HOLY_RESISTANT)) {
        type = UndeadType.HOLY_RESISTANT;
      } else if (entity.is(RESISTANT)) {
        type = UndeadType.RESISTANT;
      }
      biConsumer.accept(entity, type);
    }
  }

  @Override
  public ResourceLocation getKey(Item item) {
    return BuiltInRegistries.ITEM.getKey(item);
  }

  @Override
  public boolean isHolyTag(Item item) {
    return item.getDefaultInstance().is(HOLY_ITEMS);
  }

  @Override
  public boolean isHolyTag(EntityType<?> entity) {
    return entity.is(HOLY_ENTITIES);
  }

  @Override
  public boolean isHolyTag(MobEffect mobEffect) {
    return BuiltInRegistries.MOB_EFFECT.getTag(HOLY_EFFECTS).map(holders -> holders.stream()
        .anyMatch(mobEffectHolder -> mobEffectHolder.value() == mobEffect)).orElse(false);
  }

  @Override
  public boolean isHolyTag(Enchantment enchantment) {
    return BuiltInRegistries.ENCHANTMENT.getTag(HOLY_ENCHANTMENTS).map(holders -> holders.stream()
        .anyMatch(enchantmentHolder -> enchantmentHolder.value() == enchantment)).orElse(false);
  }

  @Override
  public boolean canSmite(ItemStack stack) {
    return false;
  }

  @Override
  public <T> RegistryProvider<T> create(ResourceKey<? extends Registry<T>> resourceKey,
                                        String modId) {
    return new Provider<>(modId, resourceKey);
  }

  @Override
  public <T> RegistryProvider<T> create(Registry<T> registry, String modId) {
    return new Provider<>(modId, registry);
  }

  private static class Provider<T> implements RegistryProvider<T> {
    private final String modId;
    private final Registry<T> registry;

    private final Set<RegistryObject<T>> entries = new HashSet<>();
    private final Set<RegistryObject<T>> entriesView = Collections.unmodifiableSet(entries);

    @SuppressWarnings({"unchecked"})
    private Provider(String modId, ResourceKey<? extends Registry<T>> key) {
      this.modId = modId;

      final var reg = BuiltInRegistries.REGISTRY.get(key.location());
      if (reg == null) {
        throw new RuntimeException("Registry with name " + key.location() + " was not found!");
      }
      registry = (Registry<T>) reg;
    }

    private Provider(String modId, Registry<T> registry) {
      this.modId = modId;
      this.registry = registry;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I extends T> RegistryObject<I> register(String name, Supplier<? extends I> supplier) {
      final var rl = new ResourceLocation(modId, name);
      final var obj = Registry.register(registry, rl, supplier.get());
      final var ro = new RegistryObject<I>() {
        final ResourceKey<I> key =
            ResourceKey.create((ResourceKey<? extends Registry<I>>) registry.key(), rl);

        @Override
        public ResourceKey<I> getResourceKey() {
          return key;
        }

        @Override
        public ResourceLocation getId() {
          return rl;
        }

        @Override
        public I get() {
          return obj;
        }

        @Override
        public Holder<I> asHolder() {
          return (Holder<I>) registry.getHolderOrThrow((ResourceKey<T>) this.key);
        }
      };
      entries.add((RegistryObject<T>) ro);
      return ro;
    }

    @Override
    public Collection<RegistryObject<T>> getEntries() {
      return entriesView;
    }

    @Override
    public String getModId() {
      return modId;
    }
  }
}
