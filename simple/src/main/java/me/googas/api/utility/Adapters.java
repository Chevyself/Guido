package me.googas.api.utility;

import com.google.gson.GsonBuilder;
import me.googas.api.messaging.Message;
import me.googas.net.sockets.json.adapters.MessageDeserializer;

public class Adapters {

  public static GsonBuilder buildClient() {
    return new GsonBuilder()
        // Required by messengers
        .registerTypeAdapter(Message.class, new MessageDeserializer());
  }
}
