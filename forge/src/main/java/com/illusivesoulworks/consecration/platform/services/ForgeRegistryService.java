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

package com.illusivesoulworks.consecration.platform.services;

import com.illusivesoulworks.consecration.ConsecrationConstants;
import com.illusivesoulworks.consecration.api.UndeadType;
import com.illusivesoulworks.consecration.common.registry.RegistryObject;
import com.illusivesoulworks.consecration.common.registry.RegistryProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;

public class ForgeRegistryService implements IRegistryService {

  private static final TagKey<EntityType<?>> UNDEAD =
      Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.tags())
          .createOptionalTagKey(new ResourceLocation(ConsecrationConstants.MOD_ID, "undead"),
              Set.of());
  private static final TagKey<EntityType<?>> FIRE_RESISTANT =
      Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.tags()).createOptionalTagKey(
          new ResourceLocation(ConsecrationConstants.MOD_ID, "fire_resistant"), Set.of());
  private static final TagKey<EntityType<?>> HOLY_RESISTANT =
      Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.tags()).createOptionalTagKey(
          new ResourceLocation(ConsecrationConstants.MOD_ID, "holy_resistant"), Set.of());
  private static final TagKey<EntityType<?>> RESISTANT =
      Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.tags())
          .createOptionalTagKey(new ResourceLocation(ConsecrationConstants.MOD_ID, "resistant"),
              Set.of());

  private static final TagKey<Item> HOLY_ITEMS =
      Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).createOptionalTagKey(
          new ResourceLocation(ConsecrationConstants.MOD_ID, ConsecrationConstants.Registry.HOLY),
          Set.of());
  private static final TagKey<MobEffect> HOLY_EFFECTS =
      Objects.requireNonNull(ForgeRegistries.MOB_EFFECTS.tags()).createOptionalTagKey(
          new ResourceLocation(ConsecrationConstants.MOD_ID, ConsecrationConstants.Registry.HOLY),
          Set.of());
  private static final TagKey<Enchantment> HOLY_ENCHANTMENTS =
      Objects.requireNonNull(ForgeRegistries.ENCHANTMENTS.tags()).createOptionalTagKey(
          new ResourceLocation(ConsecrationConstants.MOD_ID, ConsecrationConstants.Registry.HOLY),
          Set.of());
  private static final TagKey<EntityType<?>> HOLY_ENTITIES =
      Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.tags()).createOptionalTagKey(
          new ResourceLocation(ConsecrationConstants.MOD_ID, ConsecrationConstants.Registry.HOLY),
          Set.of());

  private static final ToolAction SMITE_ACTION = ToolAction.get("smite");

  @Override
  public <T> RegistryProvider<T> create(ResourceKey<? extends Registry<T>> resourceKey,
                                        String modId) {
    final var containerOpt = ModList.get().getModContainerById(modId);
    if (containerOpt.isEmpty()) {
      throw new NullPointerException("Cannot find mod container for id " + modId);
    }
    final var cont = containerOpt.get();
    if (cont instanceof FMLModContainer fmlModContainer) {
      final var register = DeferredRegister.create(resourceKey, modId);
      register.register(fmlModContainer.getEventBus());
      return new Provider<>(modId, register);
    } else {
      throw new ClassCastException("The container of the mod " + modId + " is not a FML one!");
    }
  }

  @Override
  public DamageSource getDamageSource(String id) {
    return new DamageSource(id);
  }

  @Override
  public void processUndeadTypes(BiConsumer<EntityType<?>, UndeadType> biConsumer) {
    ITagManager<EntityType<?>> tagManager = ForgeRegistries.ENTITY_TYPES.tags();

    if (tagManager != null) {

      for (EntityType<?> entity : ForgeRegistries.ENTITY_TYPES) {
        UndeadType type = UndeadType.NOT;

        if (tagManager.getTag(UNDEAD).contains(entity)) {
          type = UndeadType.DEFAULT;
        } else if (tagManager.getTag(FIRE_RESISTANT).contains(entity)) {
          type = UndeadType.FIRE_RESISTANT;
        } else if (tagManager.getTag(HOLY_RESISTANT).contains(entity)) {
          type = UndeadType.HOLY_RESISTANT;
        } else if (tagManager.getTag(RESISTANT).contains(entity)) {
          type = UndeadType.RESISTANT;
        }
        biConsumer.accept(entity, type);
      }
    }
  }

  @Override
  public ResourceLocation getKey(Item item) {
    return ForgeRegistries.ITEMS.getKey(item);
  }

  @Override
  public boolean isHolyTag(Item item) {
    return Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).getTag(HOLY_ITEMS).contains(item);
  }

  @Override
  public boolean isHolyTag(EntityType<?> entity) {
    return Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.tags()).getTag(HOLY_ENTITIES)
        .contains(entity);
  }

  @Override
  public boolean isHolyTag(MobEffect mobEffect) {
    return Objects.requireNonNull(ForgeRegistries.MOB_EFFECTS.tags()).getTag(HOLY_EFFECTS)
        .contains(mobEffect);
  }

  @Override
  public boolean isHolyTag(Enchantment enchantment) {
    return Objects.requireNonNull(ForgeRegistries.ENCHANTMENTS.tags()).getTag(HOLY_ENCHANTMENTS)
        .contains(enchantment);
  }

  @Override
  public boolean canSmite(ItemStack stack) {
    return stack.canPerformAction(SMITE_ACTION);
  }

  private static class Provider<T> implements RegistryProvider<T> {
    private final String modId;
    private final DeferredRegister<T> registry;

    private final Set<RegistryObject<T>> entries = new HashSet<>();
    private final Set<RegistryObject<T>> entriesView = Collections.unmodifiableSet(entries);

    private Provider(String modId, DeferredRegister<T> registry) {
      this.modId = modId;
      this.registry = registry;
    }

    @Override
    public String getModId() {
      return modId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I extends T> RegistryObject<I> register(String name, Supplier<? extends I> supplier) {
      final var obj = registry.<I>register(name, supplier);
      final var ro = new RegistryObject<I>() {

        @Override
        public ResourceKey<I> getResourceKey() {
          return obj.getKey();
        }

        @Override
        public ResourceLocation getId() {
          return obj.getId();
        }

        @Override
        public I get() {
          return obj.get();
        }

        @Override
        public Holder<I> asHolder() {
          return obj.getHolder().orElseThrow();
        }
      };
      entries.add((RegistryObject<T>) ro);
      return ro;
    }

    @Override
    public Set<RegistryObject<T>> getEntries() {
      return entriesView;
    }
  }
}
