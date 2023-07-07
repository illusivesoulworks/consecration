package com.illusivesoulworks.consecration.common.integration;

import com.illusivesoulworks.consecration.api.ConsecrationApi;
import com.illusivesoulworks.consecration.api.ConsecrationImc;
import java.util.function.BiFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.InterModComms;

public class WerewolvesModule extends AbstractCompatibilityModule {

  @Override
  public void enqueueImc() {
    InterModComms.sendTo("consecration", ConsecrationImc.HOLY_ATTACK.getId(),
        () -> (BiFunction<LivingEntity, DamageSource, Boolean>) (livingEntity, damageSource) -> {

          if (damageSource.getDirectEntity() instanceof LivingEntity) {
            ItemStack stack = ((LivingEntity) damageSource.getDirectEntity())
                .getMainHandItem();

            // Can hardcode this to silver since that's the only relevant oil currently
            // Will need to adapt if the mod adds other materials as oils
            if (ConsecrationApi.getInstance().isHolyMaterial("silver")) {
              CompoundTag tag = stack.getTagElement("weapon_oil");

              if (tag != null) {
                String oil = tag.getString("oil");
                return oil.contains("silver_oil_1") || oil.contains("silver_oil_2");
              }
            }
          }
          return false;
        });
  }
}
