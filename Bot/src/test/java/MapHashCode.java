import java.util.HashMap;
import java.util.HashSet;
import me.googas.api.links.LinkedDataType;
import me.googas.bot.handlers.data.types.GuidoLinkedData;
import me.googas.bot.handlers.data.types.maps.GuidoLinkedValuesMap;
import me.googas.bot.handlers.data.types.maps.GuidoValuesMap;

public class MapHashCode {

  public static void main(String[] args) {
    GuidoValuesMap map1 =
        new GuidoValuesMap("id", 86321059636203520L).addValue("guild", 755269005316456490L);
    GuidoValuesMap map2 =
        new GuidoValuesMap("id", 318189449206956043L).addValue("guild", 755269005316456490L);
    System.out.println(map1.hashCode());
    System.out.println(map2.hashCode());

    GuidoLinkedData link1 =
        new GuidoLinkedData(
            false,
            LinkedDataType.MINECRAFT,
            "a",
            map1,
            new GuidoValuesMap(),
            new HashMap<>(),
            new HashSet<>());
    GuidoLinkedData link2 =
        new GuidoLinkedData(
            false,
            LinkedDataType.MINECRAFT,
            "a",
            map2,
            new GuidoLinkedValuesMap(),
            new HashMap<>(),
            new HashSet<>());
    System.out.println(link1.hashCode());
    System.out.println(link2.hashCode());
  }
}
