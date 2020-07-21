package top.theillusivec4.consecration.api;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.util.DamageSource;
import top.theillusivec4.consecration.api.ConsecrationApi.UndeadType;

public interface IHolyRegistry {

  void addHolyProtection(BiFunction<LivingEntity, DamageSource, Integer> func);

  List<BiFunction<LivingEntity, DamageSource, Integer>> getHolyProtection();

  void addHolyMaterial(String material);

  Set<String> getHolyMaterials();

  void addHolyDamage(String damageType);

  Set<String> getHolyDamage();

  void addHolyEnchantment(String enchantment);

  void addHolyEnchantment(Enchantment enchantment);

  Set<Enchantment> getHolyEnchantments();

  void addHolyItem(String item);

  void addHolyItem(Item item);

  Set<Item> getHolyItems();

  void addHolyEffect(String potion);

  void addHolyEffect(Effect potion);

  Set<Effect> getHolyEffects();

  void addHolyEntity(String entity);

  void addHolyEntity(EntityType<?> entityType);

  Set<EntityType<?>> getHolyEntities();

  void addHolyAttack(BiFunction<LivingEntity, DamageSource, Boolean> func);

  List<BiFunction<LivingEntity, DamageSource, Boolean>> getHolyAttacks();

  void addUndead(String string);

  void addUndead(EntityType<?> entityType);

  void addUndead(EntityType<?> entityType, String undeadType);

  void addUndead(EntityType<?> entityType, UndeadType undeadType);

  Map<EntityType<?>, UndeadType> getUndead();

  DamageSource causeHolyDamage(@Nonnull Entity entity);

  DamageSource causeIndirectHolyDamage(@Nonnull Entity source, @Nullable Entity indirect);

  DamageSource causeHolyDamage();

  void clear();
}
