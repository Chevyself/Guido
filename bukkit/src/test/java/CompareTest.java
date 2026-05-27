import java.util.HashMap;
import java.util.HashSet;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableType;
import me.googas.api.utility.Maps;

public class CompareTest {

  public static void main(String[] args) {
    Linkable id =
        new Linkable(
            LinkableType.MINECRAFT,
            Maps.singleton("id", 123),
            new HashMap<>(),
            new HashMap<>(),
            new HashSet<>(),
            new HashMap<>(),
            new HashMap<>(),
            null);
    System.out.println(
        "id.compare(LinkableType.MINECRAFT, Maps.singleton(\"id\", 123)) = "
            + id.compare(LinkableType.MINECRAFT, Maps.singleton("id", 123), new HashMap<>()));
    System.out.println(
        "id.compare(LinkableType.MINECRAFT, Maps.singleton(\"id\", 1234)) = "
            + id.compare(LinkableType.MINECRAFT, Maps.singleton("id", 1234), new HashMap<>()));
    System.out.println(
        "id.compare(LinkableType.MINECRAFT, Maps.singleton(\"id\", 1235)) = "
            + id.compare(LinkableType.MINECRAFT, Maps.singleton("id", 1235), new HashMap<>()));
  }
}
