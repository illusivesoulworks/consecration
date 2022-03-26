package top.theillusivec4.consecration.client;

import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.consecration.api.ConsecrationApi;
import top.theillusivec4.consecration.common.registry.ConsecrationRegistry;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = ConsecrationApi.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ConsecrationRenderer {

  @SubscribeEvent
  public static void rendererRegistering(final EntityRenderersEvent.RegisterRenderers evt) {
    evt.registerEntityRenderer(ConsecrationRegistry.FIRE_ARROW_TYPE.get(),
        TippableArrowRenderer::new);
  }
}
