package top.theillusivec4.consecration.common.network;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import top.theillusivec4.consecration.api.ConsecrationApi;

public class ConsecrationNetwork {

  private static final String PTC_VERSION = "1";

  public static SimpleChannel INSTANCE;

  private static int id = 0;

  public static void setup() {
    INSTANCE =
        NetworkRegistry.ChannelBuilder.named(new ResourceLocation(ConsecrationApi.MOD_ID, "main"))
            .networkProtocolVersion(() -> PTC_VERSION).clientAcceptedVersions(PTC_VERSION::equals)
            .serverAcceptedVersions(PTC_VERSION::equals).simpleChannel();

    register(SPacketSetVulnerability.class, SPacketSetVulnerability::encode,
        SPacketSetVulnerability::decode, SPacketSetVulnerability::handle);
  }

  private static <M> void register(Class<M> messageType, BiConsumer<M, FriendlyByteBuf> encoder,
                                   Function<FriendlyByteBuf, M> decoder,
                                   BiConsumer<M, Supplier<NetworkEvent.Context>> messageConsumer) {
    INSTANCE.registerMessage(id++, messageType, encoder, decoder, messageConsumer);
  }

  public static void syncVulnerability(Entity entity, int duration) {
    ConsecrationNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity),
        new SPacketSetVulnerability(entity.getId(), duration));
  }
}
