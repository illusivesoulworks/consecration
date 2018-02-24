/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.client;

import c4.consecration.Consecration;
import c4.consecration.common.entities.EntityFireArrow;
import c4.consecration.common.entities.EntityFireBomb;
import c4.consecration.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class RenderFireArrow extends RenderArrow<EntityFireArrow>
{
    public static final Factory FACTORY = new Factory();

    public RenderFireArrow(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
    }

    protected ResourceLocation getEntityTexture(@Nonnull EntityFireArrow entity)
    {
        return new ResourceLocation(Consecration.MODID, "textures/items/fire_arrow.png");
    }

    public static class Factory implements IRenderFactory<EntityFireArrow> {

        @Override
        public Render<? super EntityFireArrow> createRenderFor(RenderManager manager) {
            return new RenderFireArrow(manager);
        }
    }
}
