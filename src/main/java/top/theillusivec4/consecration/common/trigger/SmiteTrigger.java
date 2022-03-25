/*
 * Copyright (c) 2017-2020 C4
 *
 * This file is part of Consecration, a mod made for Minecraft.
 *
 * Consecration is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Consecration is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Consecration.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.consecration.common.trigger;

import com.google.gson.JsonObject;
import javax.annotation.Nonnull;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;
import top.theillusivec4.consecration.Consecration;
import top.theillusivec4.consecration.common.trigger.SmiteTrigger.Instance;

public class SmiteTrigger extends SimpleCriterionTrigger<Instance> {

  public static final SmiteTrigger INSTANCE = new SmiteTrigger();
  public static final ResourceLocation ID = new ResourceLocation(Consecration.MODID, "smite");

  @Nonnull
  @Override
  public ResourceLocation getId() {
    return ID;
  }

  @Nonnull
  @Override
  public Instance createInstance(@Nonnull JsonObject p_230241_1_,
      @Nonnull EntityPredicate.Composite p_230241_2_,
      @Nonnull DeserializationContext p_230241_3_) {
    return new Instance(p_230241_2_);
  }

  public void trigger(ServerPlayer player) {
    this.trigger(player, (p_241523_0_) -> true);
  }

  public static class Instance extends AbstractCriterionTriggerInstance {

    public Instance(EntityPredicate.Composite p_i232007_1_) {
      super(ID, p_i232007_1_);
    }
  }
}