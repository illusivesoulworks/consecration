package com.illusivesoulworks.consecration.common;

import com.illusivesoulworks.consecration.api.ConsecrationApi;
import com.illusivesoulworks.consecration.api.IUndying;
import com.illusivesoulworks.consecration.common.config.ConsecrationConfig;
import java.util.Optional;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class ConsecrationEvents {

  public static float onDamaged(LivingEntity livingEntity, DamageSource source, float damage) {

    if (!livingEntity.getLevel().isClientSide()) {
      float[] result = {damage};
      ConsecrationApi api = ConsecrationApi.getInstance();
      api.getUndying(livingEntity)
          .ifPresentOrElse(undying -> result[0] = undying.onDamaged(source, damage),
              () -> {
                if (source.getDirectEntity() instanceof LivingEntity attacker) {
                  Optional<IUndying> attackerUndying = api.getUndying(attacker);

                  attackerUndying.ifPresent(undying -> {
                    int level = api.getHolyProtectionLevel(attacker, livingEntity, source);

                    if (level > 0 &&
                        livingEntity.getLevel().getRandom().nextFloat() < 0.15F * (float) level) {
                      undying.setVulnerableDuration(
                          ConsecrationConfig.CONFIG.holyVulnerableDuration.get());
                    }
                  });
                }
              });
      return result[0];
    }
    return damage;
  }
}
