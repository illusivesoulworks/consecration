package top.theillusivec4.consecration.common.capability;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.consecration.api.ConsecrationApi;
import top.theillusivec4.consecration.api.IUndying;
import top.theillusivec4.consecration.api.UndeadType;
import top.theillusivec4.consecration.api.VulnerabilityType;
import top.theillusivec4.consecration.common.ConsecrationConfig;
import top.theillusivec4.consecration.common.UndeadTypes;

public class UndyingCapability {

  public static final Capability<IUndying> INSTANCE =
      CapabilityManager.get(new CapabilityToken<>() {
      });

  public static final ResourceLocation ID = new ResourceLocation(ConsecrationApi.MOD_ID, "undying");

  private static final Map<LivingEntity, LazyOptional<IUndying>> SERVER_CACHE = new HashMap<>();
  private static final Map<LivingEntity, LazyOptional<IUndying>> CLIENT_CACHE = new HashMap<>();

  private static Map<LivingEntity, LazyOptional<IUndying>> getCache(Level level) {
    return level.isClientSide() ? CLIENT_CACHE : SERVER_CACHE;
  }

  public static LazyOptional<IUndying> get(final LivingEntity livingEntity) {

    if (!isUndying(livingEntity)) {
      return LazyOptional.empty();
    }
    Level level = livingEntity.getLevel();
    Map<LivingEntity, LazyOptional<IUndying>> cache = getCache(level);
    LazyOptional<IUndying> optional = cache.get(livingEntity);

    if (optional == null) {
      optional = livingEntity.getCapability(INSTANCE);
      cache.put(livingEntity, optional);
      optional.addListener(self -> cache.remove(livingEntity));
    }
    return optional;
  }

  private static boolean isUndying(final LivingEntity livingEntity) {
    return isValidCreature(livingEntity) && isValidDimension(
        livingEntity.getCommandSenderWorld().dimension().location());
  }

  private static boolean isValidCreature(final LivingEntity livingEntity) {
    return (ConsecrationConfig.giveDefaultUndeadUndying &&
        livingEntity.getMobType() == MobType.UNDEAD) ||
        ConsecrationApi.getInstance().getUndeadType(livingEntity) != UndeadType.NOT;
  }

  private static boolean isValidDimension(final ResourceLocation resourceLocation) {
    Set<ResourceLocation> dimensions = ConsecrationConfig.dimensions;
    ConsecrationConfig.PermissionMode permissionMode = ConsecrationConfig.dimensionsPermission;

    if (dimensions.isEmpty()) {
      return true;
    } else if (permissionMode == ConsecrationConfig.PermissionMode.BLACKLIST) {
      return !dimensions.contains(resourceLocation);
    } else {
      return dimensions.contains(resourceLocation);
    }
  }

  public static VulnerabilityType createVulnerability(LivingEntity target, DamageSource source) {
    UndeadType undeadType = UndeadTypes.get(target);
    ConsecrationApi consecration = ConsecrationApi.getInstance();

    if (undeadType == UndeadType.RESISTANT) {
      return VulnerabilityType.NONE;
    }

    if (!target.getType().fireImmune() && source.isFire() &&
        undeadType != UndeadType.FIRE_RESISTANT) {
      return VulnerabilityType.FIRE;
    }

    if (source.getDirectEntity() instanceof LivingEntity damager) {
      ItemStack stack = damager.getMainHandItem();
      Item item = stack.getItem();

      if (item instanceof TieredItem tieredItem) {
        Tier tier = tieredItem.getTier();

        for (ItemStack mat : tier.getRepairIngredient().getItems()) {
          ResourceLocation resourceLocation = mat.getItem().getRegistryName();

          if (resourceLocation != null &&
              consecration.isHolyMaterial(resourceLocation.toString())) {
            return VulnerabilityType.HOLY;
          }
        }
      }

      if (consecration.isHolyItem(stack)) {
        return VulnerabilityType.HOLY;
      }

      for (Enchantment enchantment : EnchantmentHelper.getEnchantments(stack).keySet()) {

        if (consecration.isHolyEnchantment(enchantment)) {
          return VulnerabilityType.HOLY;
        }
      }
    }

    if (consecration.isHolyDamage(source.getMsgId()) ||
        consecration.isHolyEntity(source.getDirectEntity()) ||
        consecration.isHolyAttack(target, source)) {
      return VulnerabilityType.HOLY;
    }
    return VulnerabilityType.NONE;
  }
}
