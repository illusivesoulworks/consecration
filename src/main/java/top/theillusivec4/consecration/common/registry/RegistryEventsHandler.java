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

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.consecration.api.ConsecrationApi;
import top.theillusivec4.consecration.common.enchantment.ShadowProtection;
import top.theillusivec4.consecration.common.entity.FireArrowEntity;
import top.theillusivec4.consecration.common.item.FireArrowItem;
import top.theillusivec4.consecration.common.item.FireStickItem;
import top.theillusivec4.consecration.common.potion.HolyEffect;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEventsHandler {

  @SubscribeEvent
  public static void registerItems(final RegistryEvent.Register<Item> evt) {
    evt.getRegistry().registerAll(new FireStickItem(), new FireArrowItem());
  }

  @SubscribeEvent
  public static void registerEntities(final RegistryEvent.Register<EntityType<?>> evt) {
    EntityType<?> fireArrow = EntityType.Builder.<FireArrowEntity>of(FireArrowEntity::new,
        MobCategory.MISC).sized(0.5F, 0.3F).fireImmune().setTrackingRange(64)
        .setUpdateInterval(5).setShouldReceiveVelocityUpdates(true)
        .build(RegistryReference.FIRE_ARROW);
    fireArrow.setRegistryName(RegistryReference.FIRE_ARROW);
    evt.getRegistry().register(fireArrow);
  }

  @SubscribeEvent
  public static void registerPotions(final RegistryEvent.Register<Potion> evt) {
    evt.getRegistry().registerAll(new Potion(ConsecrationApi.HOLY_ID,
        new MobEffectInstance(ConsecrationRegistry.HOLY_EFFECT, 1, 0))
        .setRegistryName(RegistryReference.HOLY), new Potion(ConsecrationApi.HOLY_ID,
        new MobEffectInstance(ConsecrationRegistry.HOLY_EFFECT, 1, 1))
        .setRegistryName(RegistryReference.STRONG_HOLY));
  }

  @SubscribeEvent
  public static void registerEffects(final RegistryEvent.Register<MobEffect> evt) {
    evt.getRegistry().register(new HolyEffect());
  }

  @SubscribeEvent
  public static void registerEnchantments(final RegistryEvent.Register<Enchantment> evt) {
    evt.getRegistry().register(new ShadowProtection());
  }
}
