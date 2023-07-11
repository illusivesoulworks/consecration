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

package com.illusivesoulworks.consecration.common.impl;

import com.illusivesoulworks.consecration.api.UndeadType;
import com.illusivesoulworks.consecration.common.config.ConsecrationConfig;
import com.illusivesoulworks.consecration.platform.Services;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;

public class UndeadTypes {

  private static final Map<EntityType<?>, UndeadType> TYPE_TO_UNDEAD_TYPE = new HashMap<>();

  private static boolean initialized = false;

  public static void init() {
    TYPE_TO_UNDEAD_TYPE.clear();
    Services.REGISTRY.processUndeadTypes(TYPE_TO_UNDEAD_TYPE::put);
    initialized = true;
  }

  public static UndeadType getType(final LivingEntity livingEntity) {

    if (!initialized) {
      init();
    }
    return TYPE_TO_UNDEAD_TYPE.getOrDefault(livingEntity.getType(), UndeadType.NOT);
  }

  public static boolean isUndying(final LivingEntity livingEntity) {
    return isValidCreature(livingEntity) &&
        isValidDimension(livingEntity.level().dimension().location());
  }

  private static boolean isValidCreature(final LivingEntity livingEntity) {
    return (ConsecrationConfig.CONFIG.giveDefaultUndeadUndying.get() &&
        livingEntity.getMobType() == MobType.UNDEAD) || getType(livingEntity) != UndeadType.NOT;
  }

  private static boolean isValidDimension(final ResourceLocation resourceLocation) {
    Set<ResourceLocation> dimensions =
        ConsecrationConfig.CONFIG.dimensionsList.get().stream().map(ResourceLocation::new)
            .collect(Collectors.toSet());
    ConsecrationConfig.ListType listType = ConsecrationConfig.CONFIG.dimensionsListType.get();

    if (dimensions.isEmpty()) {
      return true;
    } else if (listType == ConsecrationConfig.ListType.DENY) {
      return !dimensions.contains(resourceLocation);
    } else {
      return dimensions.contains(resourceLocation);
    }
  }
}
