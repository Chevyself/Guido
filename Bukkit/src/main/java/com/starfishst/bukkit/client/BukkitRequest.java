package com.starfishst.bukkit.client;

import com.starfishst.bukkit.api.Guido;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.NonNull;
import me.googas.commons.maps.MapBuilder;
import me.googas.messaging.Request;
import me.googas.messaging.api.MessengerListenFailException;
import me.googas.messaging.json.client.JsonClient;

/**
 * An extension for requests in bungee
 *
 * @param <T> the type of object that is being requested
 */
public class BukkitRequest<T> extends Request<T> {

  /**
   * Create the request
   *
   * @param clazz the class that is being requested
   * @param method the method of the request
   * @param parameters the method to get the receptor
   */
  public BukkitRequest(
      @NonNull Class<T> clazz, @NonNull String method, @NonNull MapBuilder<String, ?> parameters) {
    super(clazz, method, parameters.build());
  }

  /**
   * Create the request
   *
   * @param clazz the class that is being requested
   * @param method the method of the request
   * @param parameters the method to get the receptor
   */
  public BukkitRequest(
      @NonNull Class<T> clazz, @NonNull String method, @NonNull Map<String, ?> parameters) {
    super(clazz, method, parameters);
  }

  /**
   * Create the request
   *
   * @param clazz the class that is being requested
   * @param method the id of the request
   */
  public BukkitRequest(@NonNull Class<T> clazz, @NonNull String method) {
    super(clazz, method);
  }

  /**
   * Send a request and with the given consumer process the given object
   *
   * @param consumer the consumer for the given object
   * @param exception the consumer for the exception in case there's one
   */
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

  /**
   * Send a request and with the given consumer process the given object
   *
   * @param consumer the consumer for the given object
   */
  public void send(@NonNull Consumer<Optional<T>> consumer) {
    this.send(consumer, null);
  }

  /**
   * Send the request and return the object
   *
   * @return the object requested or null
   * @throws MessengerListenFailException in case something goes wrong in the request
   */
  public T send() throws MessengerListenFailException {
    JsonClient connection = Guido.getClient().getConnection();
    if (connection != null) {
      return connection.sendRequest(this);
    }
    return null;
  }
}
