/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.items;

import c4.consecration.Consecration;
import c4.consecration.common.entities.EntityFireArrow;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class ItemFireArrow extends ItemArrow {

    public ItemFireArrow() {
        super();
        this.setRegistryName("fire_arrow");
        this.setTranslationKey(Consecration.MODID + ".fire_arrow");
    }

    @Override
    @Nonnull
    public EntityArrow createArrow(@Nonnull World worldIn, @Nonnull ItemStack stack, EntityLivingBase shooter)
    {
        return new EntityFireArrow(worldIn, shooter);
    }

    @SuppressWarnings("ConstantConditions")
    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(),
                "inventory"));
    }
}
