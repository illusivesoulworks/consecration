package top.theillusivec4.consecration.common.integration;

import java.util.function.BiFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.InterModComms;
import se.mickelus.tetra.items.modular.IModularItem;
import top.theillusivec4.consecration.api.ConsecrationApi;
import top.theillusivec4.consecration.api.ConsecrationImc;

public class TetraModule extends AbstractCompatibilityModule {

  @Override
  public void enqueueImc() {
    InterModComms.sendTo("consecration", ConsecrationImc.HOLY_ATTACK.getId(),
        () -> (BiFunction<LivingEntity, DamageSource, Boolean>) (livingEntity, damageSource) -> {

          if (damageSource.getDirectEntity() instanceof LivingEntity) {
            ItemStack stack = ((LivingEntity) damageSource.getDirectEntity())
                .getMainHandItem();

            if (stack.getItem() instanceof IModularItem && stack.hasTag()) {
              CompoundTag compound = stack.getTag();

              if (compound != null) {

                for (String key : compound.getAllKeys()) {

                  if (key.contains("_material")) {
                    String value = compound.getString(key);
                    String[] split = value.split("/");

                    if (split.length > 1) {

                      if (ConsecrationApi.getInstance().isHolyMaterial(split[1])) {
                        return true;
                      }
                    }
                  }
                }
              }
            }
          }
          return false;
        });
  }
}
