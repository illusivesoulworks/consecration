package top.theillusivec4.consecration.common;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.consecration.api.ConsecrationApi;
import top.theillusivec4.consecration.api.ConsecrationApi.UndeadType;
import top.theillusivec4.consecration.api.IHolyRegistry;

public class HolyRegistry implements IHolyRegistry {

  // Map of entity types to undead types (normal, unholy, absolute)
  private static Map<EntityType<?>, UndeadType> undeadMap = new HashMap<>();
  // List of BiFunction taking the attacker and damage source and returning true if holy
  private static List<BiFunction<LivingEntity, DamageSource, Boolean>> holyAttacks = new ArrayList<>();
  // List of BiFunction taking the target and damage source and returning level of holiness
  private static List<BiFunction<LivingEntity, DamageSource, Integer>> holyProtection = new ArrayList<>();
  // Set of entities that can smite undead
  private static Set<EntityType<?>> holyEntities = new HashSet<>();
  // Set of potion effects that can smite undead
  private static Set<MobEffect> holyEffects = new HashSet<>();
  // Set of items that can smite undead either wearing or using it
  private static Set<Item> holyItems = new HashSet<>();
  // Set of weapon enchantments that can smite undead
  private static Set<Enchantment> holyEnchantments = new HashSet<>();
  // Set of damage types that can smite undead
  private static Set<String> holyDamage = new HashSet<>();
  // Set of materials on items or armor that can smite undead
  private static Set<String> holyMaterials = new HashSet<>();

  @Override
  public void addHolyProtection(BiFunction<LivingEntity, DamageSource, Integer> func) {
    holyProtection.add(func);
  }

  @Override
  public List<BiFunction<LivingEntity, DamageSource, Integer>> getHolyProtection() {
    return ImmutableList.copyOf(holyProtection);
  }

  @Override
  public void addHolyMaterial(String material) {
    holyMaterials.add(material);
  }

  @Override
  public Set<String> getHolyMaterials() {
    return ImmutableSet.copyOf(holyMaterials);
  }

  @Override
  public void addHolyDamage(String damageType) {
    holyDamage.add(damageType);
  }

  @Override
  public Set<String> getHolyDamage() {
    return ImmutableSet.copyOf(holyDamage);
  }

  @Override
  public void addHolyEnchantment(String enchantment) {
    Enchantment type = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantment));

    if (type != null) {
      addHolyEnchantment(type);
    }
  }

  @Override
  public void addHolyEnchantment(Enchantment enchantment) {
    holyEnchantments.add(enchantment);
  }

  @Override
  public Set<Enchantment> getHolyEnchantments() {
    return ImmutableSet.copyOf(holyEnchantments);
  }

  @Override
  public void addHolyItem(String item) {
    Item type = ForgeRegistries.ITEMS.getValue(new ResourceLocation(item));

    if (type != null) {
      addHolyItem(type);
    }
  }

  @Override
  public void addHolyItem(Item item) {
    holyItems.add(item);
  }

  @Override
  public Set<Item> getHolyItems() {
    return ImmutableSet.copyOf(holyItems);
  }

  @Override
  public void addHolyEffect(String potion) {
    MobEffect type = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(potion));

    if (type != null) {
      addHolyEffect(type);
    }
  }

  @Override
  public void addHolyEffect(MobEffect potion) {
    holyEffects.add(potion);
  }

  @Override
  public Set<MobEffect> getHolyEffects() {
    return ImmutableSet.copyOf(holyEffects);
  }

  @Override
  public void addHolyEntity(String entity) {
    EntityType.byString(entity).ifPresent(ConsecrationApi.getHolyRegistry()::addHolyEntity);
  }

  @Override
  public void addHolyEntity(EntityType<?> entityType) {
    holyEntities.add(entityType);
  }

  @Override
  public Set<EntityType<?>> getHolyEntities() {
    return ImmutableSet.copyOf(holyEntities);
  }

  @Override
  public void addHolyAttack(BiFunction<LivingEntity, DamageSource, Boolean> func) {
    holyAttacks.add(func);
  }

  @Override
  public List<BiFunction<LivingEntity, DamageSource, Boolean>> getHolyAttacks() {
    return ImmutableList.copyOf(holyAttacks);
  }

  @Override
  public void addUndead(String string) {
    ConsecrationParser.getUndeadType(string)
        .ifPresent(tuple -> addUndead(tuple.getA(), tuple.getB()));
  }

  @Override
  public void addUndead(EntityType<?> entityType) {
    addUndead(entityType, UndeadType.NORMAL);
  }

  @Override
  public void addUndead(EntityType<?> entityType, String undeadType) {
    if (undeadType.equals("unholy")) {
      addUndead(entityType, UndeadType.UNHOLY);
    } else if (undeadType.equals("absolute")) {
      addUndead(entityType, UndeadType.ABSOLUTE);
    } else {
      addUndead(entityType, UndeadType.NORMAL);
    }
  }

  @Override
  public void addUndead(EntityType<?> entityType, UndeadType undeadType) {
    undeadMap.putIfAbsent(entityType, undeadType);
  }

  @Override
  public Map<EntityType<?>, UndeadType> getUndead() {
    return ImmutableMap.copyOf(undeadMap);
  }

  @Override
  public DamageSource causeHolyDamage(@Nonnull Entity entity) {
    return new EntityDamageSource(ConsecrationApi.HOLY_ID, entity).setMagic();
  }

  @Override
  public DamageSource causeIndirectHolyDamage(@Nonnull Entity source, @Nullable Entity indirect) {
    return new IndirectEntityDamageSource(ConsecrationApi.HOLY_ID, source, indirect)
        .setMagic();
  }

  @Override
  public DamageSource causeHolyDamage() {
    return new DamageSource(ConsecrationApi.HOLY_ID).setMagic();
  }

  @Override
  public void clear() {
    undeadMap.clear();
    holyAttacks.clear();
    holyProtection.clear();
    holyEntities.clear();
    holyEffects.clear();
    holyItems.clear();
    holyEnchantments.clear();
    holyDamage.clear();
    holyMaterials.clear();
  }
}
