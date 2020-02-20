package top.theillusivec4.consecration.common.util;

import java.util.List;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.LivingEntity;
import top.theillusivec4.consecration.common.ConsecrationConfig;
import top.theillusivec4.consecration.common.ConsecrationConfig.PermissionMode;

public class UndeadHelper {

  public static boolean isValidSmiteTarget(final LivingEntity livingEntity) {
    return livingEntity.getCreatureAttribute() == CreatureAttribute.UNDEAD && isValidDimension(
        livingEntity.getEntityWorld().dimension.getType().getId());
  }

  public static boolean isValidDimension(final int id) {
    List<Integer> dimensions = ConsecrationConfig.SERVER.dimensions.get();
    PermissionMode permissionMode = ConsecrationConfig.SERVER.dimensionPermission.get();

    if (dimensions.isEmpty()) {
      return true;
    } else if (permissionMode == PermissionMode.BLACKLIST) {
      return !dimensions.contains(id);
    } else {
      return dimensions.contains(id);
    }
  }
}
