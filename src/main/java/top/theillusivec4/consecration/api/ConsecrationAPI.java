package top.theillusivec4.consecration.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IndirectEntityDamageSource;

public class ConsecrationAPI {

  public static final String HOLY_ID = "holy";

  public static DamageSource causeHolyDamage(@Nonnull Entity entity) {
    return new EntityDamageSource(HOLY_ID, entity).setMagicDamage();
  }

  public static DamageSource causeIndirectHolyDamage(@Nonnull Entity source, @Nullable Entity indirect) {
    return new IndirectEntityDamageSource(HOLY_ID, source, indirect).setMagicDamage();
  }

  public static DamageSource causeHolyDamage() {
    return new DamageSource(HOLY_ID).setMagicDamage();
  }
}
