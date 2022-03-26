package top.theillusivec4.consecration.common.integration;

import java.util.function.BiFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.InterModComms;
import net.silentchaos512.gear.api.part.PartDataList;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import top.theillusivec4.consecration.api.ConsecrationApi;

public class SilentGearModule extends AbstractModule {

  @Override
  public void enqueueImc() {
    InterModComms.sendTo("consecration", ConsecrationApi.IMC.HOLY_ATTACK,
        () -> (BiFunction<LivingEntity, DamageSource, Boolean>) (livingEntity, damageSource) -> {

          if (damageSource.getDirectEntity() instanceof LivingEntity) {
            ItemStack stack = ((LivingEntity) damageSource.getDirectEntity())
                .getMainHandItem();
            return containsHolyMaterial(stack);
          }
          return false;
        });
    InterModComms.sendTo("consecration", ConsecrationApi.IMC.HOLY_PROTECTION,
        () -> (BiFunction<LivingEntity, DamageSource, Integer>) (livingEntity, damageSource) -> {
          int level = 0;

          for (ItemStack stack : livingEntity.getArmorSlots()) {

            if (containsHolyMaterial(stack)) {
              level++;
            }
          }
          return level;
        });
  }

  private static boolean containsHolyMaterial(ItemStack stack) {

    if (GearHelper.isGear(stack)) {
      PartDataList data = GearData.getConstructionParts(stack);

      for (PartData partData : data) {
        ItemStack crafting = partData.getItem();
        CompoundTag tag = crafting.getTag();

        if (tag != null) {
          ListTag list = tag.getList("Materials", Tag.TAG_COMPOUND);

          for (Tag inbt : list) {
            CompoundTag entry = (CompoundTag) inbt;

            if (containsHolyMaterial(new ResourceLocation(entry.getString("ID")))) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  private static boolean containsHolyMaterial(ResourceLocation resourceLocation) {

    for (String mat : ConsecrationApi.getHolyRegistry().getHolyMaterials()) {
      String pattern = "^" + mat + "(\\b|[_-]\\w*)";

      if (resourceLocation.getPath().matches(pattern)) {
        return true;
      }
    }
    return false;
  }
}
