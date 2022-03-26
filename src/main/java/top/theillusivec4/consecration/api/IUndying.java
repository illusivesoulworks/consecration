package top.theillusivec4.consecration.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface IUndying {

  boolean isVulnerable();

  int getVulnerableDuration();

  void setVulnerableDuration(int duration);

  void decrementVulnerability();

  void readTag(CompoundTag tag);

  LivingEntity getLivingEntity();

  CompoundTag writeTag();
}
