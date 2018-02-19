/*
 * Copyright (c) 2017 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common;

import c4.consecration.init.ModItems;
import c4.consecration.init.ModPotions;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.*;

public class CommonEventHandler {

    @SubscribeEvent
    public void onThrowable(ProjectileImpactEvent.Throwable evt) {

        if (!evt.getThrowable().world.isRemote) {

            EntityThrowable entityThrowable = evt.getThrowable();

            if (entityThrowable instanceof EntityPotion) {
                ItemStack stack = ((EntityPotion) entityThrowable).getPotion();
                List<PotionEffect> list = PotionUtils.getEffectsFromStack(stack);
                boolean holyPotion = false;
                if (list.isEmpty()) {
                    return;
                } else {
                    for (PotionEffect effect : list) {
                        if (effect.getPotion() == ModPotions.HOLY_POTION) {
                            holyPotion = true;
                            break;
                        }
                    }
                }
                if (!holyPotion) {
                    return;
                }

                AxisAlignedBB axisalignedbb = entityThrowable.getEntityBoundingBox().grow(4.0D, 2.0D, 4.0D);
                List<EntityItem> items = entityThrowable.world.getEntitiesWithinAABB(EntityItem.class, axisalignedbb,
                        (apply) -> apply.getItem().getItem() == Items.GLOWSTONE_DUST);

                if (items.isEmpty()) {
                    return;
                }

                for (EntityItem item : items) {
                    EntityItem newItem = new EntityItem(item.world, item.posX, item.posY, item.posZ, new ItemStack(ModItems.blessedDust, item.getItem().getCount()));
                    item.world.spawnEntity(newItem);
                    item.setDead();
                }
            }
        }
    }
}
