import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Set;
import me.googas.api.client.data.permissions.SimplePermission;
import me.googas.api.client.data.permissions.SimplePermissionStack;
import me.googas.api.links.LinkableType;
import me.googas.api.permissions.PermissionStack;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.bot.core.links.GuidoLinkable;
import me.googas.bot.core.util.Mongo;
import me.googas.commons.Lots;

public class PermissionStackSerializationtest {

  public static void main(String[] args) {
    PermissionStack stack =
        new SimplePermissionStack("a context", Lots.set(new SimplePermission("Asd", true, -1)));
    Set<PermissionStack> set = Lots.set(stack);
    GuidoLinkable linkable =
        new GuidoLinkable(
            LinkableType.MINECRAFT,
            new GuidoValuesMap("nickname", "Selfie"),
            null,
            new GuidoValuesMap("uuid", "id"),
            new GuidoValuesMap(),
            new HashMap<>(),
            set);
    Gson gson = Mongo.constructGson(true);
    System.out.println(gson.toJson(set));
    System.out.println(gson.toJson(stack));
    System.out.println(gson.toJson(linkable));
  }
}
