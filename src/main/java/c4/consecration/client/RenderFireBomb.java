/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.client;

import c4.consecration.common.entities.EntityFireBomb;
import c4.consecration.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderFireBomb extends RenderSnowball<EntityFireBomb>
{
    public static final Factory FACTORY = new Factory();

    public RenderFireBomb(RenderManager renderManagerIn, RenderItem itemRendererIn)
    {
        super(renderManagerIn, ModItems.fireBomb, itemRendererIn);
    }

    public static class Factory implements IRenderFactory<EntityFireBomb> {

        @Override
        public Render<? super EntityFireBomb> createRenderFor(RenderManager manager) {
            return new RenderFireBomb(manager, Minecraft.getMinecraft().getRenderItem());
        }
    }
}
