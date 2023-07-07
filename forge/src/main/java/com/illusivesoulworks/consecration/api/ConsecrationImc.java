package com.illusivesoulworks.consecration.api;

public enum ConsecrationImc {
  HOLY_ATTACK("holy_attack"),
  HOLY_PROTECTION("holy_protection");

  final String id;

  ConsecrationImc(String id) {
    this.id = id;
  }

  public String getId() {
    return this.id;
  }
}
