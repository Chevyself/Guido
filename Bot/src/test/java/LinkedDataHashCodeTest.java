import java.util.ArrayList;
import java.util.List;
import me.googas.api.links.LinkedDataType;
import me.googas.api.links.LinkedInfo;
import me.googas.bot.core.types.GuidoLinkedInfo;
import me.googas.bot.core.types.maps.GuidoValuesMap;

public class LinkedDataHashCodeTest {

  public static void main(String[] args) {
    List<LinkedInfo> data = new ArrayList<>();

    GuidoLinkedInfo selfie =
        new GuidoLinkedInfo(
            LinkedDataType.MINECRAFT,
            new GuidoValuesMap("uuid", "5eed208dde5840229ba76ccb5ea7e92a")
                .addValue("nickname", "Selfie"));
    GuidoLinkedInfo xinoo =
        new GuidoLinkedInfo(
            LinkedDataType.MINECRAFT,
            new GuidoValuesMap("uuid", "554ee0077cc64ae589fb60d0e1e75871")
                .addValue("nickname", "xinoooo"));
    data.add(selfie);
    // data.add(xinoo);
    System.out.println(data.contains(xinoo));
  }
}
