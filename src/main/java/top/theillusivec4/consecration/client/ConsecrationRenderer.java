package top.theillusivec4.consecration.client;

import net.minecraft.client.renderer.entity.TippedArrowRenderer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import top.theillusivec4.consecration.common.registry.ConsecrationRegistry;

public class ConsecrationRenderer {

  public static void register() {
    RenderingRegistry
        .registerEntityRenderingHandler(ConsecrationRegistry.FIRE_ARROW_TYPE, TippedArrowRenderer::new);
  }
}
