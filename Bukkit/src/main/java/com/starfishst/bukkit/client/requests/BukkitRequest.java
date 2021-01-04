package com.starfishst.bukkit.client.requests;

import com.starfishst.bukkit.api.Guido;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.NonNull;
import me.googas.commons.maps.MapBuilder;
import me.googas.messaging.Request;
import me.googas.messaging.api.MessengerListenFailException;
import me.googas.messaging.json.client.JsonClient;

public class BukkitRequest<T> extends Request<T> {

  public BukkitRequest(
      @NonNull Class<T> clazz, @NonNull String method, @NonNull MapBuilder<String, ?> parameters) {
    super(clazz, method, parameters.build());
  }

  public BukkitRequest(
      @NonNull Class<T> clazz, @NonNull String method, @NonNull Map<String, ?> parameters) {
    super(clazz, method, parameters);
  }

  public BukkitRequest(@NonNull Class<T> clazz, @NonNull String method) {
    super(clazz, method);
  }

  public void send(@NonNull Consumer<Optional<T>> consumer, Consumer<Throwable> exception) {
    JsonClient connection = Guido.getClient().getConnection();
    if (connection != null) {
      if (exception != null) {
        connection.sendRequest(this, consumer, exception);
      } else {
        connection.sendRequest(this, consumer);
      }
    }
  }

  public void sendIfPresent(@NonNull Consumer<T> consumer) {
    this.send(optional -> optional.ifPresent(consumer));
  }

  public void sendIfPresent(@NonNull Consumer<T> consumer, @NonNull Consumer<Throwable> exception) {
    this.send(optional -> optional.ifPresent(consumer), exception);
  }

  public void send(@NonNull Consumer<Optional<T>> consumer) {
    this.send(consumer, null);
  }

  public T send() throws MessengerListenFailException {
    JsonClient connection = Guido.getClient().getConnection();
    if (connection != null) {
      return connection.sendRequest(this);
    }
    return null;
  }
}
