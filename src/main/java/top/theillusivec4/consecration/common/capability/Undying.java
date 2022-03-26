package top.theillusivec4.consecration.common.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import top.theillusivec4.consecration.api.IUndying;
import top.theillusivec4.consecration.common.network.ConsecrationNetwork;

public class Undying implements IUndying {

  private static final String SMITE_TAG = "smite";

  private final LivingEntity livingEntity;

  private int vulnerableDuration = 0;

  public Undying(LivingEntity livingEntity) {
    this.livingEntity = livingEntity;
  }

  @Override
  public boolean isVulnerable() {
    return this.vulnerableDuration > 0;
  }
  @Override
  public int getVulnerableDuration() {
    return this.vulnerableDuration;
  }

  @Override
  public void setVulnerableDuration(int duration) {
    this.vulnerableDuration = duration;
    LivingEntity livingEntity = this.getLivingEntity();

    if (!livingEntity.getLevel().isClientSide()) {
      ConsecrationNetwork.syncVulnerability(livingEntity, duration);
    }
  }

  @Override
  public void decrementVulnerability() {

    if (this.vulnerableDuration > 0) {
      this.vulnerableDuration--;
    }
  }

  @Override
  public void readTag(CompoundTag tag) {
    this.setVulnerableDuration(tag.getInt(SMITE_TAG));
  }

  @Override
  public LivingEntity getLivingEntity() {
    return this.livingEntity;
  }

  @Override
  public CompoundTag writeTag() {
    CompoundTag tag = new CompoundTag();
    tag.putInt(SMITE_TAG, this.getVulnerableDuration());
    return tag;
  }
}
