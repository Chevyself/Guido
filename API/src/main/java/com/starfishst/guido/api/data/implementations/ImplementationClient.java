package com.starfishst.guido.api.data.implementations;

import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.function.Predicate;
import me.googas.commons.cache.Cache;
import me.googas.commons.cache.ICatchable;
import me.googas.commons.gson.GsonProvider;
import me.googas.messaging.Request;
import me.googas.messaging.api.Message;
import me.googas.messaging.json.adapters.MessageDeserializer;
import me.googas.messaging.json.client.JsonClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The client used by implementation to connect with Guido */
public class ImplementationClient {

  /** The ip of the bot */
  @NotNull private static final String ip = "localhost";

  /** The port of the bot */
  private static final int port = 3000;
  /** The token that will give access to read or writing */
  @NotNull private String token;
  /** The client to connect with the bot */
  @Nullable private JsonClient client;

  /**
   * Create the client
   *
   * @param token the token
   */
  public ImplementationClient(@NotNull String token) {
    this.token = token;
  }

  /**
   * Connects the client with the bot
   *
   * @return the stabilised connection
   * @throws IOException if the bot cannot be reached
   */
  @NotNull
  public JsonClient startConnection() throws IOException {
    client =
        new JsonClient(
            new Socket(ip, port),
            Throwable::printStackTrace,
            new GsonBuilder()
                .registerTypeAdapter(Message.class, new MessageDeserializer())
                .setPrettyPrinting()
                .create(),
            1000);
    client.start();
    GsonProvider.refresh();
    return client;
  }

  /**
   * Set the json client
   *
   * @param client the new value of json client
   */
  public void setClient(@Nullable JsonClient client) {
    this.client = client;
  }

  /**
   * Send a request or get the object from cache
   *
   * @param request the request to make in case the object is not in cache
   * @param predicate the boolean to get the object from cache
   * @param consumer the method to do after we get the object
   * @param <T> the type of the object
   */
  public <T extends ICatchable> void request(
      @NotNull Request<T> request, @NotNull Predicate<T> predicate, @NotNull Consumer<T> consumer) {
    T catchable = Cache.getCatchable(request.getClazz(), predicate);
    if (catchable != null) {
      consumer.accept(catchable);
    } else {
      JsonClient connection = this.getConnection();
      if (connection == null) {
        try {
          connection = this.startConnection();
        } catch (IOException e) {
          throw new IllegalStateException("There's no connection with the bot", e);
        }
      }
      connection.sendRequest(request, consumer);
    }
  }

  /**
   * Set the token that the client should use
   *
   * @param token the new token
   */
  public void setToken(@NotNull String token) {
    this.token = token;
  }

  /**
   * Get the json client for messaging
   *
   * @return the json client
   */
  @Nullable
  public JsonClient getConnection() {
    return client;
  }

  /**
   * Get the token that the client is using
   *
   * @return the token
   */
  @NotNull
  public String getToken() {
    return token;
  }
}
