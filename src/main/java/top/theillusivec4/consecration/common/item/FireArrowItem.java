package top.theillusivec4.consecration.common.item;

import javax.annotation.Nonnull;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import top.theillusivec4.consecration.common.entity.FireArrowEntity;
import top.theillusivec4.consecration.common.registry.RegistryReference;

public class FireArrowItem extends ArrowItem {

  public FireArrowItem() {
    super(new Item.Properties().group(ItemGroup.COMBAT));
    this.setRegistryName(RegistryReference.FIRE_ARROW);
  }

  @Nonnull
  @Override
  public AbstractArrowEntity createArrow(@Nonnull World worldIn, @Nonnull ItemStack stack,
      LivingEntity shooter) {
    return new FireArrowEntity(worldIn, shooter);
  }
}
