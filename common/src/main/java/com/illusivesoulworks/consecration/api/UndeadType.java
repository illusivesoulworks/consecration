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

package com.illusivesoulworks.consecration.api;

public enum UndeadType {
  NOT("not"),
  DEFAULT("default"),
  FIRE_RESISTANT("fire_resistant"),
  HOLY_RESISTANT("holy_resistant"),
  RESISTANT("resistant");

  final String id;

  UndeadType(String id) {
    this.id = id;
  }

  public String getId() {
    return this.id;
  }
}
