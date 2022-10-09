package top.theillusivec4.consecration.common.integration;

import de.teamlapen.werewolves.core.ModOils;
import java.util.function.BiFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.InterModComms;
import top.theillusivec4.consecration.api.ConsecrationApi;
import top.theillusivec4.consecration.api.ConsecrationImc;

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
