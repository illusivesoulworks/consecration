package top.theillusivec4.consecration.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;
import top.theillusivec4.consecration.api.ConsecrationApi;
import top.theillusivec4.consecration.api.UndeadType;

public class UndeadTypes {

  private static final TagKey<EntityType<?>> UNDEAD =
      Objects.requireNonNull(ForgeRegistries.ENTITIES.tags())
          .createOptionalTagKey(new ResourceLocation(ConsecrationApi.MOD_ID, "undead"),
              new HashSet<>());
  private static final TagKey<EntityType<?>> FIRE_RESISTANT =
      Objects.requireNonNull(ForgeRegistries.ENTITIES.tags())
          .createOptionalTagKey(new ResourceLocation(ConsecrationApi.MOD_ID, "fire_resistant"),
              new HashSet<>());
  private static final TagKey<EntityType<?>> HOLY_RESISTANT =
      Objects.requireNonNull(ForgeRegistries.ENTITIES.tags())
          .createOptionalTagKey(new ResourceLocation(ConsecrationApi.MOD_ID, "holy_resistant"),
              new HashSet<>());
  private static final TagKey<EntityType<?>> RESISTANT =
      Objects.requireNonNull(ForgeRegistries.ENTITIES.tags())
          .createOptionalTagKey(new ResourceLocation(ConsecrationApi.MOD_ID, "resistant"),
              new HashSet<>());
  private static final Map<EntityType<?>, UndeadType> TYPE_TO_UNDEAD_TYPE = new HashMap<>();

  private static boolean initialized = false;

  public static void init() {
    ITagManager<EntityType<?>> tagManager = ForgeRegistries.ENTITIES.tags();

    if (tagManager != null) {

      for (EntityType<?> entity : ForgeRegistries.ENTITIES) {
        UndeadType type = UndeadType.NOT;

        if (tagManager.getTag(UNDEAD).contains(entity)) {
          type = UndeadType.DEFAULT;
        } else if (tagManager.getTag(FIRE_RESISTANT).contains(entity)) {
          type = UndeadType.FIRE_RESISTANT;
        } else if (tagManager.getTag(HOLY_RESISTANT).contains(entity)) {
          type = UndeadType.HOLY_RESISTANT;
        } else if (tagManager.getTag(RESISTANT).contains(entity)) {
          type = UndeadType.RESISTANT;
        }
        TYPE_TO_UNDEAD_TYPE.put(entity, type);
      }
    }
    initialized = true;
  }

  public static UndeadType get(final LivingEntity livingEntity) {

    if (!initialized) {
      init();
    }
    return TYPE_TO_UNDEAD_TYPE.getOrDefault(livingEntity.getType(), UndeadType.NOT);
  }
}
