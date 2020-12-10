package me.googas.bot.core.server.request;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import lombok.NonNull;
import me.googas.bot.core.Guido;
import me.googas.messaging.Request;
import me.googas.messaging.api.MessengerListenFailException;
import me.googas.messaging.json.JsonMessenger;
import me.googas.messaging.json.server.JsonClientThread;

/**
 * An extension of request for guido
 *
 * @param <T> the object which is being requested
 */
public class GuidoRequest<T> extends Request<T> {

  /**
   * Create the request
   *
   * @param clazz the class of the object that is being requested
   * @param id the unique id of the request
   * @param method the method which will be used to get the receptor to process the request
   * @param parameters the parameters that will use the receptor to process the request
   */
  public GuidoRequest(
      @NonNull Class<T> clazz,
      @NonNull UUID id,
      @NonNull String method,
      @NonNull Map<String, ?> parameters) {
    super(clazz, id, method, parameters);
  }

  /**
   * Create the request. It generates a random id
   *
   * @param clazz the class of the object that is being requested
   * @param method the method which will be used to get the receptor to process the request
   * @param parameters the parameters that will use the receptor to process the request
   */
  public GuidoRequest(
      @NonNull Class<T> clazz, @NonNull String method, @NonNull Map<String, ?> parameters) {
    super(clazz, method, parameters);
  }

  /**
   * Create the request. It generates a random id and has no parameters
   *
   * @param clazz the class of the object that is being requested
   * @param method the method which will be used to get the receptor to process the request
   */
  public GuidoRequest(@NonNull Class<T> clazz, @NonNull String method) {
    super(clazz, method);
  }

  public HashMap<JsonMessenger, T> broadcast() {
    return Guido.getServer().sendRequest(this);
  }

  /**
   * Send the request to the bungee server
   *
   * @return the returned object by bungee
   * @throws MessengerListenFailException in case that the bungee fails to process the information
   */
  public T send() throws MessengerListenFailException {
    JsonClientThread bungee = Guido.getServer().getAuthenticator().getBungee();
    if (bungee != null) {
      return bungee.sendRequest(this);
    }
    return null;
  }

  /**
   * Broadcast the request to all the connected servers
   *
   * @param biConsumer the consumer to handle the received objects
   * @return the returned object by bungee
   */
  public void broadcast(@NonNull BiConsumer<JsonMessenger, T> biConsumer) {
    Guido.getServer().sendRequest(this, biConsumer);
  }

  /**
   * Send the request to the bungee server
   *
   * @param consumer the consumer to process the received object
   * @param exception the consumer in case of an exception
   */
  public void send(@NonNull Consumer<T> consumer, @NonNull Consumer<Throwable> exception) {
    JsonClientThread bungee = Guido.getServer().getAuthenticator().getBungee();
    if (bungee != null) {
      bungee.sendRequest(this, consumer, exception);
    }
  }

  /**
   * Send the request to the bungee server with a default exception handler
   *
   * @param consumer the consumer to process the received object
   */
  public void send(@NonNull Consumer<T> consumer) {
    JsonClientThread bungee = Guido.getServer().getAuthenticator().getBungee();
    if (bungee != null) {
      bungee.sendRequest(
          this,
          consumer,
          (exception) ->
              Guido.getLogger()
                  .log(
                      Level.SEVERE, exception, () -> "There's been an error in the bungee server"));
    }
  }
}
