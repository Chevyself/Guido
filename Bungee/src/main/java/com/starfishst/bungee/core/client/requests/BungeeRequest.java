package com.starfishst.bungee.core.client.requests;

import com.starfishst.bungee.api.Guido;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import lombok.NonNull;
import me.googas.api.client.Client;
import me.googas.commons.maps.MapBuilder;
import me.googas.messaging.Request;
import me.googas.messaging.api.MessengerListenFailException;
import me.googas.messaging.json.client.JsonClient;

/**
 * An extension for requests in bungee
 *
 * @param <T> the type of object that is being requested
 */
public class BungeeRequest<T> extends Request<T> {

  /**
   * Create the request
   *
   * @param clazz the class that is being requested
   * @param method the method of the request
   * @param parameters the method to get the receptor
   */
  public BungeeRequest(
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
  public BungeeRequest(
      @NonNull Class<T> clazz, @NonNull String method, @NonNull Map<String, ?> parameters) {
    super(clazz, method, parameters);
  }

  /**
   * Create the request
   *
   * @param clazz the class that is being requested
   * @param method the id of the request
   */
  public BungeeRequest(@NonNull Class<T> clazz, @NonNull String method) {
    super(clazz, method);
  }

  /**
   * Send a request and with the given consumer process the given object
   *
   * @param consumer the consumer for the given object
   * @param exception the consumer for the exception in case there's one
   */
  public void send(@NonNull Consumer<Optional<T>> consumer, Consumer<Throwable> exception) {
    Client connection = Guido.getClient();
    if (exception != null) {
      connection.sendRequest(this, consumer, exception);
    } else {
      connection.sendRequest(this, consumer);
    }
  }

  /**
   * Send a request and with the given consumer process the given object
   *
   * @param consumer the consumer for the given object
   */
  public void send(@NonNull Consumer<Optional<T>> consumer) {
    this.send(consumer, exception -> Guido.getLogger().log(Level.SEVERE, exception, () -> ""));
  }

  /**
   * Send a request and with the given consumer process the given object
   *
   * @param consumer the consumer for the given object
   */
  public void sendIfPresent(@NonNull Consumer<T> consumer) {
    this.send(optional -> optional.ifPresent(consumer), null);
  }

  /**
   * Send the request and return the object
   *
   * @return the object requested
   * @throws MessengerListenFailException in case something goes wrong in the request or there's not
   *     connection with the bot
   */
  public T send() throws MessengerListenFailException {
    JsonClient connection = Guido.getClient().getConnection();
    if (connection != null) {
      return connection.sendRequest(this);
    }
    throw new MessengerListenFailException("There's not connection with the bot");
  }

  /** Send the request and don't mind the result */
  public void queue() {
    this.send(t -> {});
  }
}
