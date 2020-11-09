import java.util.HashMap;
import java.util.HashSet;
import me.googas.api.links.LinkableDataType;
import me.googas.bot.core.types.GuidoLinkableData;
import me.googas.bot.core.types.maps.GuidoLinkedValuesMap;
import me.googas.bot.core.types.maps.GuidoValuesMap;

public class MapHashCode {

  public static void main(String[] args) {
    GuidoValuesMap map1 =
        new GuidoValuesMap("id", 86321059636203520L).put("guild", 755269005316456490L);
    GuidoValuesMap map2 =
        new GuidoValuesMap("id", 318189449206956043L).put("guild", 755269005316456490L);
    System.out.println(map1.hashCode());
    System.out.println(map2.hashCode());

    GuidoLinkableData link1 =
        new GuidoLinkableData(
            LinkableDataType.MINECRAFT,
            "a",
            map1,
            new GuidoValuesMap(),
            new HashMap<>(),
            new HashSet<>());
    GuidoLinkableData link2 =
        new GuidoLinkableData(
            LinkableDataType.MINECRAFT,
            "a",
            map2,
            new GuidoLinkedValuesMap(),
            new HashMap<>(),
            new HashSet<>());
    System.out.println(link1.hashCode());
    System.out.println(link2.hashCode());
  }
}
