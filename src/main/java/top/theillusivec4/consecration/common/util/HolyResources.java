package top.theillusivec4.consecration.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.consecration.common.ConsecrationConfig;
import top.theillusivec4.consecration.common.ConsecrationConfig.Server;

public class HolyResources {

  private static Map<EntityType<?>, UndeadType> undeadList = new HashMap<>();
  private static List<BiFunction<LivingEntity, DamageSource, Boolean>> holyFunctions = new ArrayList<>();

  private static Set<EntityType<?>> holyEntities = new HashSet<>();
  private static Set<Potion> holyPotions = new HashSet<>();
  private static Set<Item> holyWeapons = new HashSet<>();
  private static Set<Enchantment> holyEnchantments = new HashSet<>();
  private static Set<String> holyDamage = new HashSet<>();
  private static Set<String> holyMaterials = new HashSet<>();

  public static void seedConfigs() {
    Server config = ConsecrationConfig.SERVER;

    config.undeadList.get().forEach(entity -> {
      String[] parsed = entity.split(";");
      EntityType.byKey(entity).ifPresent(type -> {
        UndeadType undeadType = UndeadType.NORMAL;

        if (parsed.length > 1) {

          if (parsed[1].equals("unholy")) {
            undeadType = UndeadType.UNHOLY;
          } else if (parsed[1].equals("absolute")) {
            undeadType = UndeadType.ABSOLUTE;
          }
        }
        undeadList.put(type, undeadType);
      });
    });
    config.holyEntities.get().forEach(entity -> {
      EntityType.byKey(entity).ifPresent(type -> holyEntities.add(type));
    });
    config.holyPotions.get().forEach(potion -> {
      Potion type = Potion.getPotionTypeForName(potion);

      if (type != Potions.EMPTY) {
        holyPotions.add(type);
      }
    });
    config.holyWeapons.get().forEach(item -> {
      Item type = ForgeRegistries.ITEMS.getValue(new ResourceLocation(item));

      if (type != null) {
        holyWeapons.add(type);
      }
    });
    config.holyEnchantments.get().forEach(enchant -> {
      Enchantment type = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchant));

      if (type != null) {
        holyEnchantments.add(type);
      }
    });
    holyDamage.addAll(config.holyDamage.get());
    holyMaterials.addAll(config.holyMaterials.get());
  }

  public static boolean containsUndead(ResourceLocation resourceLocation) {
    return undeadList.containsKey(resourceLocation);
  }

  public static UndeadType getUndeadType(ResourceLocation resourceLocation) {
    return undeadList.getOrDefault(resourceLocation, UndeadType.NORMAL);
  }

  public static boolean hasHolyEnchantment(ItemStack stack) {

    for (Enchantment enchantment : EnchantmentHelper.getEnchantments(stack).keySet()) {

      if (holyEnchantments.contains(enchantment)) {
        return true;
      }
    }
    return false;
  }

  public static DamageType processHolyFunctions(LivingEntity target, DamageSource source) {

    for (BiFunction<LivingEntity, DamageSource, Boolean> func : holyFunctions) {
      if (func.apply(target, source)) {
        return DamageType.HOLY;
      }
    }
    return DamageType.NONE;
  }

  public static boolean isHolyEntity(@Nullable Entity entity) {
    return entity != null && holyEntities.contains(entity.getType());
  }

  public static boolean isHolyDamage(DamageSource source) {
    return holyDamage.contains(source.getDamageType());
  }

  public static boolean isHolyWeapon(Item item) {
    return holyWeapons.contains(item);
  }

  public static boolean containsHolyMaterial(ResourceLocation resourceLocation) {

    for (String mat : holyMaterials) {
      String pattern = "^" + mat + "(\\b|[_-]\\w*)";
      if (resourceLocation.getPath().matches(pattern)) {
        return true;
      }
    }
    return false;
  }

  public enum UndeadType {
    NORMAL, UNHOLY, ABSOLUTE
  }

  public enum DamageType {
    NONE, FIRE, HOLY
  }
}
