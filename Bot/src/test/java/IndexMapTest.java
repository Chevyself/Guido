import java.util.Map;
import java.util.TreeMap;
import me.googas.commons.maps.MapBuilder;

public class IndexMapTest {

  public static void main(String[] args) {
    Map<Object, Object> map =
        new MapBuilder<>(new TreeMap<>())
            .append(6, "gflgf")
            .append(1, "askldfajsdf")
            .append(7, "asdasdasd")
            .append(0, "asdoksadklasdkljsdf")
            .build();
    System.out.println(map);
  }
}
