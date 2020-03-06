package top.theillusivec4.consecration.common.entity;

import javax.annotation.Nonnull;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import top.theillusivec4.consecration.common.registry.ConsecrationRegistry;

public class FireArrowEntity extends ArrowEntity {

  public FireArrowEntity(EntityType<? extends ArrowEntity> type, World world) {
    super(type, world);
  }

  public FireArrowEntity(World worldIn, double x, double y, double z) {
    super(worldIn, x, y, z);
  }

  public FireArrowEntity(World worldIn, LivingEntity shooter) {
    super(worldIn, shooter);
  }

  @Override
  public void tick() {
    super.tick();
    this.setFire(100);
  }

  @Override
  @Nonnull
  protected ItemStack getArrowStack() {
    return new ItemStack(ConsecrationRegistry.FIRE_ARROW);
  }
}
