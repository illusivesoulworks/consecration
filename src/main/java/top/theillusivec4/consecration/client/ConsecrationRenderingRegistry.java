package top.theillusivec4.consecration.client;

import net.minecraft.client.renderer.entity.TippedArrowRenderer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import top.theillusivec4.consecration.common.entity.FireArrowEntity;

public class ConsecrationRenderingRegistry {

  public static void register() {
    RenderingRegistry
        .registerEntityRenderingHandler(FireArrowEntity.class, TippedArrowRenderer::new);
  }
}
