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

package top.theillusivec4.consecration.common.registry;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Potion;
import net.minecraftforge.registries.ObjectHolder;
import top.theillusivec4.consecration.Consecration;

@ObjectHolder(Consecration.MODID)
public class ConsecrationRegistry {

  @ObjectHolder(RegistryReference.HOLY)
  public static final Effect HOLY_EFFECT;

  @ObjectHolder(RegistryReference.HOLY)
  public static final Potion HOLY_POTION;

  @ObjectHolder(RegistryReference.STRONG_HOLY)
  public static final Potion STRONG_HOLY_POTION;

  @ObjectHolder(RegistryReference.FIRE_ARROW)
  public static final Item FIRE_ARROW;

  @ObjectHolder(RegistryReference.FIRE_ARROW)
  public static final EntityType<?> FIRE_ARROW_TYPE;

  static {
    HOLY_EFFECT = null;
    HOLY_POTION = null;
    STRONG_HOLY_POTION = null;
    FIRE_ARROW = null;
    FIRE_ARROW_TYPE = null;
  }
}
