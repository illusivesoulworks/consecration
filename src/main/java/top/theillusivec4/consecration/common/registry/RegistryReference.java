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

import top.theillusivec4.consecration.Consecration;
import top.theillusivec4.consecration.api.ConsecrationAPI;

public class RegistryReference {

  public static final String HOLY = Consecration.MODID + ":" + ConsecrationAPI.HOLY_ID;
  public static final String STRONG_HOLY =
      Consecration.MODID + ":strong_" + ConsecrationAPI.HOLY_ID;
  public static final String SHADOW_PROTECTION = Consecration.MODID + ":shadow_protection";
  public static final String FIRE_STICK = Consecration.MODID + ":fire_stick";
  public static final String FIRE_ARROW = Consecration.MODID + ":fire_arrow";
}
