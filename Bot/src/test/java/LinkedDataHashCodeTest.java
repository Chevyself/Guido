import java.util.ArrayList;
import java.util.List;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.bot.core.types.GuidoLinkableInfo;
import me.googas.bot.core.types.maps.GuidoValuesMap;

public class LinkedDataHashCodeTest {

  public static void main(String[] args) {
    List<LinkableInfo> data = new ArrayList<>();

    GuidoLinkableInfo selfie =
        new GuidoLinkableInfo(
            LinkableType.MINECRAFT,
            new GuidoValuesMap("uuid", "5eed208dde5840229ba76ccb5ea7e92a")
                .put("nickname", "Selfie"));
    GuidoLinkableInfo xinoo =
        new GuidoLinkableInfo(
            LinkableType.MINECRAFT,
            new GuidoValuesMap("uuid", "554ee0077cc64ae589fb60d0e1e75871")
                .put("nickname", "xinoooo"));
    data.add(selfie);
    // data.add(xinoo);
    System.out.println(data.contains(xinoo));
  }
}
