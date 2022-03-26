package top.theillusivec4.consecration.api;

public enum UndeadType {
  NOT("not"),
  DEFAULT("default"),
  FIRE_RESISTANT("fire_resistant"),
  HOLY_RESISTANT("holy_resistant"),
  RESISTANT("resistant");

  String id;

  UndeadType(String id) {
    this.id = id;
  }

  public String getId() {
    return this.id;
  }
}
