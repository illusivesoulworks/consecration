package top.theillusivec4.consecration.common.capability;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.consecration.common.capability.UndyingCapability.IUndying;
import top.theillusivec4.consecration.common.util.UndeadHelper;

public class CapabilityEventsHandler {

  @SubscribeEvent
  public void attachCapabilities(final AttachCapabilitiesEvent<Entity> evt) {
    if (evt.getObject() instanceof LivingEntity && UndeadHelper
        .isValidSmiteTarget((LivingEntity) evt.getObject())) {
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

      undyingOpt.ifPresent(undying -> {
        DamageSource source = evt.getSource();
        Entity entity = source.getImmediateSource();
        if (source.isFireDamage()) {
          undying.setSmiteDuration(200);
        } else if (entity instanceof LivingEntity) {
          ItemStack stack = ((LivingEntity) entity).getHeldItemMainhand();

          if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SMITE, stack) > 0) {
            undying.setSmiteDuration(200);
          }
        }
      });
    }
  }
}
