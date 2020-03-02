package top.theillusivec4.consecration.common.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.consecration.common.capability.UndyingCapability;
import top.theillusivec4.consecration.common.capability.UndyingCapability.IUndying;
import top.theillusivec4.consecration.common.registry.RegistryReference;

public class ShadowProtection extends Enchantment {

  public ShadowProtection() {
    super(Rarity.UNCOMMON, EnchantmentType.ARMOR,
        new EquipmentSlotType[]{EquipmentSlotType.CHEST, EquipmentSlotType.FEET,
            EquipmentSlotType.HEAD, EquipmentSlotType.LEGS});
    this.setRegistryName(RegistryReference.SHADOW_PROTECTION);
  }

  public int getMinEnchantability(int enchantmentLevel) {
    return 8 + (enchantmentLevel - 1) * 6;
  }

  public int getMaxEnchantability(int enchantmentLevel) {
    return this.getMinEnchantability(enchantmentLevel) + 6;
  }

  public int getMaxLevel() {
    return 4;
  }

  public int calcModifierDamage(int level, DamageSource source) {

    if (source.canHarmInCreative()) {
      return 0;
    }
    Entity entity = source.getTrueSource();

    if (entity instanceof LivingEntity) {
      LazyOptional<IUndying> undyingOpt = UndyingCapability.getCapability((LivingEntity) entity);
      return undyingOpt.map(undying -> level * 2).orElse(0);
    }
    return 0;
  }

  public boolean canApplyTogether(Enchantment ench) {

    if (ench instanceof ProtectionEnchantment) {
      ProtectionEnchantment protectionenchantment = (ProtectionEnchantment) ench;
      return protectionenchantment.protectionType == ProtectionEnchantment.Type.FALL;
    }
    return super.canApplyTogether(ench);
  }
}
