package top.theillusivec4.consecration.common.potion;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.logging.log4j.Level;
import top.theillusivec4.consecration.Consecration;
import top.theillusivec4.consecration.api.ConsecrationAPI;
import top.theillusivec4.consecration.common.capability.UndyingCapability;
import top.theillusivec4.consecration.common.capability.UndyingCapability.IUndying;
import top.theillusivec4.consecration.common.registry.RegistryReference;

public class HolyEffect extends Effect {

  private static final Method START_CONVERTING = ObfuscationReflectionHelper
      .findMethod(ZombieVillagerEntity.class, "func_191991_a", UUID.class, Integer.TYPE);

  public HolyEffect() {
    super(EffectType.BENEFICIAL, 0xFFFFFF);
    this.setRegistryName(RegistryReference.HOLY);
  }

  @Override
  public void affectEntity(@Nullable Entity source, @Nullable Entity indirectSource,
      @Nonnull LivingEntity livingEntity, int amplifier, double health) {

    if (livingEntity instanceof ZombieVillagerEntity) {
      convertZombieVillager((ZombieVillagerEntity) livingEntity, indirectSource, 1800 >> amplifier);
    } else {
      LazyOptional<IUndying> undyingOpt = UndyingCapability.getCapability(livingEntity);
      undyingOpt.ifPresent(undying -> {
        if (source == null) {
          livingEntity
              .attackEntityFrom(ConsecrationAPI.causeHolyDamage(), (float) (8 << amplifier));
        } else {
          livingEntity
              .attackEntityFrom(ConsecrationAPI.causeIndirectHolyDamage(source, indirectSource),
                  (float) (8 << amplifier));
        }
      });

      if (!undyingOpt.isPresent()) {
        livingEntity.addPotionEffect(new EffectInstance(Effects.REGENERATION, 600, amplifier));
        livingEntity.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 600, amplifier));
      }
    }
  }

  @Override
  public boolean isInstant() {
    return true;
  }

  private void convertZombieVillager(ZombieVillagerEntity zombieVillager, @Nullable Entity source,
      int conversionTime) {

    if (zombieVillager.isConverting()) {
      return;
    }
    UUID uuid = source instanceof PlayerEntity ? source.getUniqueID() : null;

    try {
      START_CONVERTING
          .invoke(zombieVillager, uuid, zombieVillager.world.rand.nextInt(200) + conversionTime);
    } catch (IllegalAccessException | InvocationTargetException e) {
      Consecration.LOGGER.log(Level.ERROR, "Error in startConverting for entity " + zombieVillager);
    }
  }
}
