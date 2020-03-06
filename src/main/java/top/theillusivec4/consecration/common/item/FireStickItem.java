package top.theillusivec4.consecration.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import top.theillusivec4.consecration.common.registry.RegistryReference;

public class FireStickItem extends Item {

  public FireStickItem() {
    super(new Item.Properties().maxDamage(13).group(ItemGroup.COMBAT));
    this.setRegistryName(RegistryReference.FIRE_STICK);
  }

  @Override
  public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {

    if (!player.world.isRemote && !entity.isImmuneToFire()) {
      stack.damageItem(1, player, damager -> damager.sendBreakAnimation(Hand.MAIN_HAND));
      entity.setFire(2);
    }
    return false;
  }
}
