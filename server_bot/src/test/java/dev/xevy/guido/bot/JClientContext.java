package dev.xevy.guido.bot;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.NonNull;
import me.googas.net.api.Messenger;
import me.googas.net.sockets.json.ReceivedJsonRequest;

public class JClientContext {
  @Getter private final Messenger messenger;
  @NonNull @Getter private final ReceivedJsonRequest receivedJsonRequest;
  @NonNull @Getter private final Gson gson;

  public JClientContext(
      Messenger messenger, @NonNull ReceivedJsonRequest receivedJsonRequest, @NonNull Gson gson) {
    this.messenger = messenger;
    this.receivedJsonRequest = receivedJsonRequest;
    this.gson = gson;
  }

  @NonNull
  public <T> T get(@NonNull String name, Class<T> tClass) {
    JsonElement jsonElement = receivedJsonRequest.getParameters().get(name);
    return gson.fromJson(jsonElement, tClass);
  }
}
