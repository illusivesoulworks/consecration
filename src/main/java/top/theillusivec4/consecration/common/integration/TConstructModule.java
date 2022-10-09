package top.theillusivec4.consecration.common.integration;

import java.util.function.BiFunction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.InterModComms;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import top.theillusivec4.consecration.api.ConsecrationApi;
import top.theillusivec4.consecration.api.ConsecrationImc;

public class TConstructModule extends AbstractCompatibilityModule {

  @Override
  public void enqueueImc() {
    InterModComms.sendTo("consecration", ConsecrationImc.HOLY_ATTACK.getId(),
        () -> (BiFunction<LivingEntity, DamageSource, Boolean>) (livingEntity, damageSource) -> {
          Entity source = damageSource.getDirectEntity();

          if (source instanceof LivingEntity) {
            ItemStack stack = ((LivingEntity) source).getMainHandItem();
            MaterialIdNBT nbt = MaterialIdNBT.from(stack);
            for (MaterialVariantId material : nbt.getMaterials()) {

              if (ConsecrationApi.getInstance().isHolyMaterial(material.getId().getPath())) {
                return true;
              }
            }
          }
          return false;
        });
  }
}
