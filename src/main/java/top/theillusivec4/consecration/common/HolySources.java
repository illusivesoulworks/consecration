package top.theillusivec4.consecration.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.consecration.api.ConsecrationApi;
import top.theillusivec4.consecration.api.ConsecrationImc;
import top.theillusivec4.consecration.api.UndeadType;

public class HolySources {

  private static final ResourceLocation HOLY = new ResourceLocation(ConsecrationApi.MOD_ID, "holy");

  private static final TagKey<Item> HOLY_ITEMS =
      Objects.requireNonNull(ForgeRegistries.ITEMS.tags())
          .createOptionalTagKey(HOLY, new HashSet<>());
  private static final TagKey<MobEffect> HOLY_EFFECTS =
      Objects.requireNonNull(ForgeRegistries.MOB_EFFECTS.tags())
          .createOptionalTagKey(HOLY, new HashSet<>());
  private static final TagKey<Enchantment> HOLY_ENCHANTMENTS =
      Objects.requireNonNull(ForgeRegistries.ENCHANTMENTS.tags())
          .createOptionalTagKey(HOLY, new HashSet<>());
  private static final TagKey<EntityType<?>> HOLY_ENTITIES =
      Objects.requireNonNull(ForgeRegistries.ENTITIES.tags())
          .createOptionalTagKey(HOLY, new HashSet<>());

  private static final List<BiFunction<LivingEntity, DamageSource, Boolean>> HOLY_ATTACKS =
      new ArrayList<>();
  private static final List<BiFunction<LivingEntity, DamageSource, Integer>> HOLY_PROTECTION =
      new ArrayList<>();
  private static final Set<String> HOLY_DAMAGE = new HashSet<>();
  private static final Set<String> HOLY_MATERIALS = new HashSet<>();

  public static void setup() {
    HOLY_DAMAGE.clear();
    HOLY_DAMAGE.addAll(ConsecrationConfig.holyDamage);
    HOLY_MATERIALS.clear();
    HOLY_MATERIALS.addAll(ConsecrationConfig.holyMaterials);
  }

  @SuppressWarnings("unchecked")
  public static void processImc(Stream<InterModComms.IMCMessage> imcStream) {

    if (imcStream != null) {
      imcStream.forEach(imcMessage -> {
        Object message = imcMessage.messageSupplier().get();
        String method = imcMessage.method();

        if (message instanceof BiFunction) {

          if (method.equals(ConsecrationImc.HOLY_ATTACK.getId())) {
            HOLY_ATTACKS.add((BiFunction<LivingEntity, DamageSource, Boolean>) message);
          } else if (method.equals(ConsecrationImc.HOLY_PROTECTION.getId())) {
            HOLY_PROTECTION.add((BiFunction<LivingEntity, DamageSource, Integer>) message);
          }
        }
      });
    }
  }

  public static boolean contains(Item item) {
    return Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).getTag(HOLY_ITEMS).contains(item);
  }

  public static boolean contains(MobEffect effect) {
    return Objects.requireNonNull(ForgeRegistries.MOB_EFFECTS.tags()).getTag(HOLY_EFFECTS)
        .contains(effect);
  }

  public static boolean contains(Enchantment enchantment) {
    return Objects.requireNonNull(ForgeRegistries.ENCHANTMENTS.tags()).getTag(HOLY_ENCHANTMENTS)
        .contains(enchantment);
  }

  public static boolean contains(Entity entity) {

    if (entity == null) {
      return false;
    }
    List<MobEffectInstance> effects = new ArrayList<>();

    if (entity instanceof ThrownPotion) {
      effects.addAll(PotionUtils.getMobEffects(((ThrownPotion) entity).getItem()));
    } else if (entity instanceof AreaEffectCloud cloud) {
      effects.addAll(cloud.getPotion().getEffects());
    }

    for (MobEffectInstance effect : effects) {

      if (contains(effect.getEffect())) {
        return true;
      }
    }
    return Objects.requireNonNull(ForgeRegistries.ENTITIES.tags()).getTag(HOLY_ENTITIES)
        .contains(entity.getType());
  }

  public static int getHolyProtectionLevel(LivingEntity attacker, LivingEntity defender,
                                           DamageSource damageSource) {

    if (UndeadTypes.get(attacker) == UndeadType.RESISTANT) {
      return 0;
    }
    int level = 0;

    for (ItemStack stack : defender.getArmorSlots()) {

      if (!stack.isEmpty()) {
        Item item = stack.getItem();

        if (item instanceof ArmorItem) {
          ArmorMaterial material = ((ArmorItem) item).getMaterial();
          for (ItemStack mat : material.getRepairIngredient().getItems()) {
            ResourceLocation resourceLocation = mat.getItem().getRegistryName();

            if (resourceLocation != null && (ConsecrationApi.getInstance()
                .isHolyMaterial(resourceLocation.toString()) || ConsecrationApi.getInstance()
                .isHolyMaterial(resourceLocation.getPath()))) {
              level++;
            }
          }
        } else if (ConsecrationApi.getInstance().isHolyItem(item)) {
          level++;
        }
      }
    }

    for (BiFunction<LivingEntity, DamageSource, Integer> func : HOLY_PROTECTION) {
      level += func.apply(defender, damageSource);
    }
    return level;
  }

  public static boolean isHolyAttack(LivingEntity livingEntity, DamageSource damageSource) {

    for (BiFunction<LivingEntity, DamageSource, Boolean> func : HOLY_ATTACKS) {

      if (func.apply(livingEntity, damageSource)) {
        return true;
      }
    }
    return false;
  }

  public static boolean containsMaterial(String material) {

    for (String holyMaterial : HOLY_MATERIALS) {
      String pattern = "^" + holyMaterial + "(\\b|[_-]\\w*)";

      if (material.matches(pattern)) {
        return true;
      }
    }
    return false;
  }

  public static boolean containsDamage(String damage) {
    return HOLY_DAMAGE.contains(damage);
  }
}
