package top.theillusivec4.consecration.common.capability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.consecration.common.ConsecrationConfig;
import top.theillusivec4.consecration.common.ConsecrationUtils.DamageType;
import top.theillusivec4.consecration.common.capability.UndyingCapability.IUndying;
import top.theillusivec4.consecration.common.trigger.SmiteTrigger;
import top.theillusivec4.consecration.common.ConsecrationUtils;

public class CapabilityEventsHandler {

  @SubscribeEvent
  public void attachCapabilities(final AttachCapabilitiesEvent<Entity> evt) {

    if (evt.getObject() instanceof LivingEntity && ConsecrationUtils
        .isUndying((LivingEntity) evt.getObject())) {
      evt.addCapability(UndyingCapability.ID, new UndyingCapability.Provider());
    }
  }

  @SubscribeEvent
  public void onLivingUpdate(LivingEvent.LivingUpdateEvent evt) {
    LivingEntity livingEntity = evt.getEntityLiving();

    if (!livingEntity.getEntityWorld().isRemote) {
      LazyOptional<IUndying> undyingOpt = UndyingCapability.getCapability(livingEntity);

      undyingOpt.ifPresent(undying -> {
        if (undying.hasSmite()) {
          if (livingEntity.ticksExisted % 10 == 0) {
            ServerWorld world = (ServerWorld) livingEntity.getEntityWorld();
            world.spawnParticle(ParticleTypes.INSTANT_EFFECT, livingEntity.posX,
                livingEntity.posY + livingEntity.getHeight() / 2.0D, livingEntity.posZ, 2,
                livingEntity.getWidth() / 2.0D, livingEntity.getHeight() / 4.0D,
                livingEntity.getWidth() / 2.0D, 0.0D);
          }
          undying.tickSmite();
        }
      });
    }
  }

  @SubscribeEvent
  public void onLivingDamage(LivingDamageEvent evt) {
    LivingEntity livingEntity = evt.getEntityLiving();

    if (!livingEntity.getEntityWorld().isRemote) {
      LazyOptional<IUndying> undyingOpt = UndyingCapability.getCapability(livingEntity);

      if (!undyingOpt.isPresent() && evt.getSource().getImmediateSource() instanceof LivingEntity) {
        LivingEntity attacker = (LivingEntity) evt.getSource().getImmediateSource();
        LazyOptional<IUndying> undyingOpt2 = UndyingCapability.getCapability(attacker);

        undyingOpt2.ifPresent(undying -> {
          int level = ConsecrationUtils.protect(attacker, livingEntity, evt.getSource());

          if (level > 0 && livingEntity.getEntityWorld().rand.nextFloat() < 0.15F * (float)level ) {
            undying.setSmiteDuration(ConsecrationConfig.SERVER.holySmiteDuration.get() * 20);
          }
        });
      }

      undyingOpt.ifPresent(undying -> {
        DamageSource source = evt.getSource();

        if (source == DamageSource.OUT_OF_WORLD || source == DamageSource.CRAMMING
            || source == DamageSource.IN_WALL) {
          return;
        }
        DamageType type = ConsecrationUtils.smite(livingEntity, source);

        if (type != DamageType.NONE) {

          if (type == DamageType.FIRE) {
            undying.setSmiteDuration(ConsecrationConfig.SERVER.fireSmiteDuration.get() * 20);
          } else {
            undying.setSmiteDuration(ConsecrationConfig.SERVER.holySmiteDuration.get() * 20);
          }

          if (source.getTrueSource() instanceof ServerPlayerEntity) {
            SmiteTrigger.INSTANCE.trigger((ServerPlayerEntity) source.getTrueSource());
          }
        } else if (!source.isDamageAbsolute() && !undying.hasSmite()) {
          Entity trueSource = source.getTrueSource();

          if ((trueSource != null && (trueSource instanceof PlayerEntity
              || ConsecrationConfig.SERVER.bystanderNerf.get()))) {
            evt.setAmount(
                evt.getAmount() * (float) (1 - ConsecrationConfig.SERVER.damageReduction.get()));
          }
        }
      });
    }
  }
}
