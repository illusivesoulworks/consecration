/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of Consecration.
 * Consecration is open source and licensed under the GNU General Public License v3.
 * A copy of the license can be found here: https://www.gnu.org/licenses/gpl.txt
 */

package c4.consecration.common.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SmiteKilledTrigger implements ICriterionTrigger<AbstractCriterionInstance>
{
    private final Map<PlayerAdvancements, SmiteKilledTrigger.Listeners> listeners = Maps.newHashMap();
    private final ResourceLocation id;

    public SmiteKilledTrigger(ResourceLocation id)
    {
        this.id = id;
    }

    public ResourceLocation getId()
    {
        return this.id;
    }

    public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<AbstractCriterionInstance> listener)
    {
        SmiteKilledTrigger.Listeners killedtrigger$listeners = this.listeners.get(playerAdvancementsIn);

        if (killedtrigger$listeners == null)
        {
            killedtrigger$listeners = new SmiteKilledTrigger.Listeners(playerAdvancementsIn);
            this.listeners.put(playerAdvancementsIn, killedtrigger$listeners);
        }

        killedtrigger$listeners.add(listener);
    }

    public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<AbstractCriterionInstance> listener)
    {
        SmiteKilledTrigger.Listeners killedtrigger$listeners = this.listeners.get(playerAdvancementsIn);

        if (killedtrigger$listeners != null)
        {
            killedtrigger$listeners.remove(listener);

            if (killedtrigger$listeners.isEmpty())
            {
                this.listeners.remove(playerAdvancementsIn);
            }
        }
    }

    public void removeAllListeners(PlayerAdvancements playerAdvancementsIn)
    {
        this.listeners.remove(playerAdvancementsIn);
    }

    /**
     * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
     */
    public AbstractCriterionInstance deserializeInstance(JsonObject json, JsonDeserializationContext context)
    {
        return new AbstractCriterionInstance(this.id);
    }

    public void trigger(EntityPlayerMP playerMP)
    {
        SmiteKilledTrigger.Listeners killedtrigger$listeners = this.listeners.get(playerMP.getAdvancements());

        if (killedtrigger$listeners != null)
        {
            killedtrigger$listeners.trigger();
        }
    }

    static class Listeners
    {
        private final PlayerAdvancements playerAdvancements;
        private final Set<Listener<AbstractCriterionInstance>> listeners = Sets.newHashSet();

        public Listeners(PlayerAdvancements playerAdvancementsIn)
        {
            this.playerAdvancements = playerAdvancementsIn;
        }

        public boolean isEmpty()
        {
            return this.listeners.isEmpty();
        }

        public void add(ICriterionTrigger.Listener<AbstractCriterionInstance> listener)
        {
            this.listeners.add(listener);
        }

        public void remove(ICriterionTrigger.Listener<AbstractCriterionInstance> listener)
        {
            this.listeners.remove(listener);
        }

        public void trigger()
        {
            List<Listener<AbstractCriterionInstance>> list = null;

            for (ICriterionTrigger.Listener<AbstractCriterionInstance> listener : this.listeners)
            {
                if (list == null)
                {
                    list = Lists.newArrayList();
                }

                list.add(listener);
            }

            if (list != null)
            {
                for (ICriterionTrigger.Listener<AbstractCriterionInstance> listener1 : list)
                {
                    listener1.grantCriterion(this.playerAdvancements);
                }
            }
        }
    }
}
