package com.illusivesoulworks.consecration.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public interface IUndying {

  void tick();

  boolean isVulnerable();

  int getVulnerableDuration();

  void setVulnerableDuration(int duration);

  void onEffectAdded(MobEffectInstance effectInstance);

  float onDamaged(DamageSource source, float damage);

  void sync();

  void readTag(CompoundTag tag);

  LivingEntity getLivingEntity();

  CompoundTag writeTag();
}
