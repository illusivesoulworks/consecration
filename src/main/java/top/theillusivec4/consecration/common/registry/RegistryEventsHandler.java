package top.theillusivec4.consecration.common.registry;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.consecration.api.ConsecrationAPI;
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
    EntityType<?> fireArrow = EntityType.Builder.<FireArrowEntity>create(FireArrowEntity::new,
        EntityClassification.MISC).size(0.5F, 0.3F).immuneToFire().setTrackingRange(64)
        .setUpdateInterval(5).setShouldReceiveVelocityUpdates(true)
        .build(RegistryReference.FIRE_ARROW);
    fireArrow.setRegistryName(RegistryReference.FIRE_ARROW);
    evt.getRegistry().register(fireArrow);
  }

  @SubscribeEvent
  public static void registerPotions(final RegistryEvent.Register<Potion> evt) {
    evt.getRegistry().registerAll(new Potion(ConsecrationAPI.HOLY_ID,
        new EffectInstance(ConsecrationRegistry.HOLY_EFFECT, 1, 0))
        .setRegistryName(RegistryReference.HOLY), new Potion(ConsecrationAPI.HOLY_ID,
        new EffectInstance(ConsecrationRegistry.HOLY_EFFECT, 1, 1))
        .setRegistryName(RegistryReference.STRONG_HOLY));
  }

  @SubscribeEvent
  public static void registerEffects(final RegistryEvent.Register<Effect> evt) {
    evt.getRegistry().register(new HolyEffect());
  }

  @SubscribeEvent
  public static void registerEnchantments(final RegistryEvent.Register<Enchantment> evt) {
    evt.getRegistry().register(new ShadowProtection());
  }
}
