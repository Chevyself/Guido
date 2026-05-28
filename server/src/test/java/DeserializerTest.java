import com.google.gson.Gson;
import java.util.Map;
import me.googas.api.messaging.Message;
import me.googas.bot.adapters.MapDeserializer;
import me.googas.bot.core.util.Mongo;
import me.googas.net.api.messages.Request;
import me.googas.net.sockets.json.ReceivedJsonRequest;
import me.googas.net.sockets.json.adapters.MessageDeserializer;

public class DeserializerTest {

  public static void main(String[] args) {
    Gson gson =
        Mongo.builderGson()
            .registerTypeAdapter(Map.class, new MapDeserializer())
            .registerTypeAdapter(Message.class, new MessageDeserializer())
            .create();
    String json =
        gson.toJson(
            Request.builder(Boolean.class, "auth")
                .put("token", "a token")
                .put("test", 2.0)
                .build());
    System.out.println("json = " + json);
    ReceivedJsonRequest request = gson.fromJson(json, ReceivedJsonRequest.class);
    System.out.println("request = " + request);
  }
}
