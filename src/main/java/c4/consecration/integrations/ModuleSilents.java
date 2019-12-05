package c4.consecration.integrations;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.init.ModTraits;
import net.silentchaos512.gear.util.TraitHelper;

public class ModuleSilents extends ModuleCompatibility {

  public ModuleSilents() {
    super("silentgear");
  }

  @Override
  public boolean process(EntityLivingBase target, DamageSource source) {

    if (source.getImmediateSource() instanceof EntityLivingBase) {
      ItemStack stack = ((EntityLivingBase) source.getImmediateSource()).getHeldItemMainhand();

      if (stack.getItem() instanceof ICoreItem) {
        return TraitHelper.getTraitLevel(stack, ModTraits.holy) > 0;
      }
    }
    return false;
  }
}
