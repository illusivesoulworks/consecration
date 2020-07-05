package top.theillusivec4.consecration.common;

import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Tuple;
import top.theillusivec4.consecration.api.ConsecrationApi.UndeadType;

public class ConsecrationParser {

  public static Optional<Tuple<EntityType<?>, UndeadType>> getUndeadType(String string) {
    String[] parsed = string.split(";");
    return EntityType.byKey(string).map(type -> {
      UndeadType undeadType = UndeadType.NORMAL;

      if (parsed.length > 1) {

        if (parsed[1].equals("unholy")) {
          undeadType = UndeadType.UNHOLY;
        } else if (parsed[1].equals("absolute")) {
          undeadType = UndeadType.ABSOLUTE;
        }
      }
      return new Tuple<>(type, undeadType);
    });
  }
}
