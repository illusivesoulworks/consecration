package top.theillusivec4.consecration.common.registry;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Potion;
import net.minecraftforge.registries.ObjectHolder;
import top.theillusivec4.consecration.Consecration;

@ObjectHolder(Consecration.MODID)
public class ConsecrationRegistry {

  @ObjectHolder(RegistryReference.HOLY)
  public static final Effect HOLY_EFFECT;

  @ObjectHolder(RegistryReference.HOLY)
  public static final Potion HOLY_POTION;

  @ObjectHolder(RegistryReference.STRONG_HOLY)
  public static final Potion STRONG_HOLY_POTION;

  @ObjectHolder(RegistryReference.FIRE_ARROW)
  public static final Item FIRE_ARROW;

  @ObjectHolder(RegistryReference.FIRE_ARROW)
  public static final EntityType<?> FIRE_ARROW_TYPE;

  static {
    HOLY_EFFECT = null;
    HOLY_POTION = null;
    STRONG_HOLY_POTION = null;
    FIRE_ARROW = null;
    FIRE_ARROW_TYPE = null;
  }
}
