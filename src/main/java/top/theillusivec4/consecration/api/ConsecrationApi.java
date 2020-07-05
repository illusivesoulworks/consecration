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

package top.theillusivec4.consecration.api;

public class ConsecrationApi {

  // The identifier for the holy damage type
  public static final String HOLY_ID = "holy";

  public enum UndeadType {
    NORMAL, UNHOLY, ABSOLUTE
  }

  public static class IMC {

    public static final String UNDEAD = "undead";
    public static final String HOLY_ENTITY = "holy_entity";
    public static final String HOLY_EFFECT = "holy_effect";
    public static final String HOLY_ITEM = "holy_item";
    public static final String HOLY_ENCHANTMENT = "holy_enchantment";
    public static final String HOLY_MATERIAL = "holy_material";
    public static final String HOLY_DAMAGE = "holy_damage";
    public static final String HOLY_ATTACK = "holy_attack";
    public static final String HOLY_PROTECTION = "holy_protection";
  }

  private static IHolyRegistry holyRegistry;

  public static void setHolyRegistry(IHolyRegistry registryIn) {

    if (holyRegistry == null) {
      holyRegistry = registryIn;
    }
  }

  public static IHolyRegistry getHolyRegistry() {
    return holyRegistry;
  }
}
