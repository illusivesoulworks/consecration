package com.illusivesoulworks.consecration.common.capability;

import com.illusivesoulworks.consecration.common.impl.Undying;
import dev.onyxstudios.cca.api.v3.component.Component;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public class UndyingComponent extends Undying implements Component {

  public UndyingComponent(LivingEntity livingEntity) {
    super(livingEntity);
  }

  @Override
  public void readFromNbt(@Nonnull CompoundTag tag) {
    this.readTag(tag.getCompound("Data"));
  }

  @Override
  public void writeToNbt(@Nonnull CompoundTag tag) {
    tag.put("Data", this.writeTag());
  }
}
