package top.theillusivec4.consecration.common.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.consecration.api.ConsecrationApi;
import top.theillusivec4.consecration.api.IUndying;
import top.theillusivec4.consecration.api.UndeadType;
import top.theillusivec4.consecration.common.HolySources;
import top.theillusivec4.consecration.common.UndeadTypes;
import top.theillusivec4.consecration.common.capability.UndyingCapability;

public class ConsecrationApiImpl extends ConsecrationApi {

  @Override
  public LazyOptional<IUndying> getUndying(LivingEntity livingEntity) {
    return UndyingCapability.get(livingEntity);
  }

  @Override
  public boolean isHolyEntity(Entity entity) {
    return HolySources.contains(entity);
  }

  @Override
  public boolean isHolyItem(Item item) {
    return HolySources.contains(item);
  }

  @Override
  public boolean isHolyEnchantment(Enchantment enchantment) {
    return HolySources.contains(enchantment);
  }

  @Override
  public boolean isHolyEffect(MobEffect mobEffect) {
    return HolySources.contains(mobEffect);
  }

  @Override
  public boolean isHolyDamage(String damageType) {
    return HolySources.containsDamage(damageType);
  }

  @Override
  public boolean isHolyMaterial(String material) {
    return HolySources.containsMaterial(material);
  }

  @Override
  public int getHolyProtectionLevel(LivingEntity attacker,
                                    LivingEntity livingEntity, DamageSource damageSource) {
    return HolySources.getHolyProtectionLevel(attacker, livingEntity, damageSource);
  }

  @Override
  public boolean isHolyAttack(LivingEntity livingEntity, DamageSource damageSource) {
    return HolySources.isHolyAttack(livingEntity, damageSource);
  }

  @Override
  public DamageSource causeHolyDamage(@Nonnull Entity entity) {
    return new EntityDamageSource(ConsecrationApi.HOLY_IDENTIFIER, entity).setMagic();
  }

  @Override
  public DamageSource causeIndirectHolyDamage(@Nonnull Entity source, @Nullable Entity indirect) {
    return new IndirectEntityDamageSource(ConsecrationApi.HOLY_IDENTIFIER, source, indirect)
        .setMagic();
  }

  @Override
  public DamageSource causeHolyDamage() {
    return new DamageSource(ConsecrationApi.HOLY_IDENTIFIER).setMagic();
  }

  @Override
  public UndeadType getUndeadType(LivingEntity livingEntity) {
    return UndeadTypes.get(livingEntity);
  }
}
