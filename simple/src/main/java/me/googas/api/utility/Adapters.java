package me.googas.api.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.googas.api.adapters.permissions.WrappedPermissionAdapter;
import me.googas.api.permissions.AbstractPermission;
import me.googas.messaging.api.Message;
import me.googas.messaging.json.adapters.MessageDeserializer;

public class Adapters {

  public static Gson buildClient() {
    return new GsonBuilder()
        // Required by messengers
        .registerTypeAdapter(Message.class, new MessageDeserializer())
        // Required for requests
        .registerTypeAdapter(AbstractPermission.class, new WrappedPermissionAdapter())
        // .setPrettyPrinting() probably not needed
        .create();
  }
}
