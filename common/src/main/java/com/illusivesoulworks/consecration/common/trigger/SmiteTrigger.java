/*
 * Copyright (C) 2017-2023 Illusive Soulworks
 *
 * Consecration is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Consecration is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Consecration.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.consecration.common.trigger;

import com.google.gson.JsonObject;
import com.illusivesoulworks.consecration.ConsecrationConstants;
import com.illusivesoulworks.consecration.common.trigger.SmiteTrigger.Instance;
import javax.annotation.Nonnull;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class SmiteTrigger extends SimpleCriterionTrigger<Instance> {

  public static final SmiteTrigger INSTANCE = new SmiteTrigger();
  public static final ResourceLocation ID =
      new ResourceLocation(ConsecrationConstants.MOD_ID, "smite");

  @Nonnull
  @Override
  public ResourceLocation getId() {
    return ID;
  }

  @Nonnull
  @Override
  public Instance createInstance(@Nonnull JsonObject jsonObject,
                                 @Nonnull EntityPredicate.Composite predicate,
                                 @Nonnull DeserializationContext context) {
    return new Instance(predicate);
  }

  public void trigger(ServerPlayer player) {
    this.trigger(player, (instance) -> true);
  }

  public static class Instance extends AbstractCriterionTriggerInstance {

    public Instance(EntityPredicate.Composite predicate) {
      super(ID, predicate);
    }
  }
}