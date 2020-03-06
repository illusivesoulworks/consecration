package top.theillusivec4.consecration.common.trigger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.consecration.Consecration;

public class SmiteTrigger implements ICriterionTrigger<CriterionInstance> {

  public static final SmiteTrigger INSTANCE = new SmiteTrigger(
      new ResourceLocation(Consecration.MODID, "smite"));

  private final Map<PlayerAdvancements, Listeners> listeners = Maps.newHashMap();
  private final ResourceLocation id;

  public SmiteTrigger(ResourceLocation id) {
    this.id = id;
  }

  @Nonnull
  @Override
  public ResourceLocation getId() {
    return this.id;
  }

  @Override
  public void addListener(@Nonnull PlayerAdvancements playerAdvancementsIn,
      @Nonnull ICriterionTrigger.Listener<CriterionInstance> listener) {
    SmiteTrigger.Listeners killedtrigger$listeners = this.listeners.get(playerAdvancementsIn);

    if (killedtrigger$listeners == null) {
      killedtrigger$listeners = new SmiteTrigger.Listeners(playerAdvancementsIn);
      this.listeners.put(playerAdvancementsIn, killedtrigger$listeners);
    }

    killedtrigger$listeners.add(listener);
  }

  @Override
  public void removeListener(@Nonnull PlayerAdvancements playerAdvancementsIn,
      @Nonnull ICriterionTrigger.Listener<CriterionInstance> listener) {
    SmiteTrigger.Listeners killedtrigger$listeners = this.listeners.get(playerAdvancementsIn);

    if (killedtrigger$listeners != null) {
      killedtrigger$listeners.remove(listener);

      if (killedtrigger$listeners.isEmpty()) {
        this.listeners.remove(playerAdvancementsIn);
      }
    }
  }

  @Override
  public void removeAllListeners(@Nonnull PlayerAdvancements playerAdvancementsIn) {
    this.listeners.remove(playerAdvancementsIn);
  }

  @Nonnull
  @Override
  public CriterionInstance deserializeInstance(JsonObject json,
      JsonDeserializationContext context) {
    return new CriterionInstance(this.id);
  }

  public void trigger(ServerPlayerEntity playerMP) {
    SmiteTrigger.Listeners killedtrigger$listeners = this.listeners.get(playerMP.getAdvancements());

    if (killedtrigger$listeners != null) {
      killedtrigger$listeners.trigger();
    }
  }

  static class Listeners {

    private final PlayerAdvancements playerAdvancements;
    private final Set<Listener<CriterionInstance>> listeners = Sets.newHashSet();

    public Listeners(PlayerAdvancements playerAdvancementsIn) {
      this.playerAdvancements = playerAdvancementsIn;
    }

    public boolean isEmpty() {
      return this.listeners.isEmpty();
    }

    public void add(ICriterionTrigger.Listener<CriterionInstance> listener) {
      this.listeners.add(listener);
    }

    public void remove(ICriterionTrigger.Listener<CriterionInstance> listener) {
      this.listeners.remove(listener);
    }

    public void trigger() {
      List<Listener<CriterionInstance>> list = null;

      for (ICriterionTrigger.Listener<CriterionInstance> listener : this.listeners) {
        if (list == null) {
          list = Lists.newArrayList();
        }

        list.add(listener);
      }

      if (list != null) {
        for (ICriterionTrigger.Listener<CriterionInstance> listener1 : list) {
          listener1.grantCriterion(this.playerAdvancements);
        }
      }
    }
  }
}