import com.google.gson.Gson;
import java.util.Map;
import me.googas.api.messaging.Message;
import me.googas.bot.adapters.MapDeserializer;
import me.googas.bot.core.util.Mongo;
import me.googas.commons.maps.Maps;
import me.googas.messaging.ReceivedRequest;
import me.googas.messaging.Request;
import me.googas.messaging.json.adapters.MessageDeserializer;

public class DeserializerTest {

  public static void main(String[] args) {
    Gson gson =
        Mongo.builderGson()
            .registerTypeAdapter(Map.class, new MapDeserializer())
            .registerTypeAdapter(Message.class, new MessageDeserializer())
            .create();
    String json =
        gson.toJson(
            new Request<>(
                Boolean.class,
                "auth",
                Maps.objects("token", "a token").append("test", 2.0).build()));
    System.out.println("json = " + json);
    ReceivedRequest request = gson.fromJson(json, ReceivedRequest.class);
    System.out.println("request = " + request);
  }
}
