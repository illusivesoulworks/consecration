package top.theillusivec4.consecration.common.util;

import java.util.List;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.consecration.common.ConsecrationConfig;
import top.theillusivec4.consecration.common.ConsecrationConfig.PermissionMode;
import top.theillusivec4.consecration.common.util.HolyResources.DamageType;
import top.theillusivec4.consecration.common.util.HolyResources.UndeadType;

public class UndeadHelper {

  public static DamageType smite(LivingEntity target, DamageSource source) {
    ResourceLocation rl = target.getType().getRegistryName();
    UndeadType undeadType = HolyResources.getUndeadType(rl);

    if (undeadType == UndeadType.ABSOLUTE) {
      return DamageType.NONE;
    }

    if (!target.isImmuneToFire() && source.isFireDamage() && undeadType != UndeadType.UNHOLY) {
      return DamageType.FIRE;
    }

    if (source.getImmediateSource() instanceof LivingEntity) {
      LivingEntity damager = (LivingEntity) source.getImmediateSource();
      ItemStack stack = damager.getHeldItemMainhand();
      Item item = stack.getItem();

      if (item instanceof TieredItem) {
        TieredItem tieredItem = (TieredItem) item;
        IItemTier tier = tieredItem.getTier();

        for (ItemStack mat : tier.getRepairMaterial().getMatchingStacks()) {
          ResourceLocation resourceLocation = mat.getItem().getRegistryName();

          if (resourceLocation != null && HolyResources.containsHolyMaterial(resourceLocation)) {
            return DamageType.HOLY;
          }
        }
      } else if (HolyResources.isHolyWeapon(item)) {
        return DamageType.HOLY;
      }

      if (HolyResources.hasHolyEnchantment(stack)) {
        return DamageType.HOLY;
      }
    }

    if (HolyResources.isHolyDamage(source) || HolyResources
        .isHolyEntity(source.getImmediateSource())) {
      return DamageType.HOLY;
    }
    return HolyResources.processHolyFunctions(target, source);
  }

  public static boolean isUndying(final LivingEntity livingEntity) {
    return isValidCreature(livingEntity) && isValidDimension(
        livingEntity.getEntityWorld().dimension.getType().getId());
  }

  public static boolean isValidCreature(final LivingEntity livingEntity) {
    return (ConsecrationConfig.SERVER.defaultUndead.get()
        && livingEntity.getCreatureAttribute() == CreatureAttribute.UNDEAD) || HolyResources
        .containsUndead(livingEntity.getType().getRegistryName());
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
