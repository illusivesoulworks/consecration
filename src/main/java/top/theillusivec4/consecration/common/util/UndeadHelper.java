package top.theillusivec4.consecration.common.util;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.LivingEntity;

public class UndeadHelper {

  public static boolean isUndead(final LivingEntity livingEntity) {
    return livingEntity.getCreatureAttribute() == CreatureAttribute.UNDEAD;
  }
}
