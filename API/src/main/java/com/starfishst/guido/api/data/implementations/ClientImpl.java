package com.starfishst.guido.api.data.implementations;

import com.google.gson.GsonBuilder;
import com.starfishst.guido.api.data.implementations.receptors.ReceptorsImpl;
import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.function.Predicate;
import me.googas.commons.NullableAtomic;
import me.googas.commons.cache.Cache;
import me.googas.commons.cache.ICatchable;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import me.googas.messaging.api.Message;
import me.googas.messaging.json.adapters.MessageDeserializer;
import me.googas.messaging.json.client.JsonClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The client used by implementation to connect with Guido */
public class ClientImpl {

  /** The ip of the bot */
  @NotNull private static final String IP = "localhost";

  /** The port of the bot */
  private static final int PORT = 3000;

  /** The token that will give access to read or writing */
  @NotNull private String token;
  /** The client to connect with the bot */
  @Nullable private JsonClient client;
  /** The receptors that the client is using */
  @NotNull private final ReceptorsImpl receptors = new ReceptorsImpl(this);
  /** The handler for throwable */
  @NotNull private ThrowableHandlerImpl handler = new ThrowableHandlerImpl(this);

  /**
   * Create the client
   *
   * @param token the token
   */
  public ClientImpl(@NotNull String token) {
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
    this.client =
        new JsonClient(
            new Socket(ClientImpl.IP, ClientImpl.PORT),
            Throwable::printStackTrace,
            new GsonBuilder()
                .registerTypeAdapter(Message.class, new MessageDeserializer())
                .setPrettyPrinting()
                .create(),
            1000);
    this.client.addReceptors(this.receptors);
    this.client.start();
    this.client.sendRequest(
        new Request<>(Boolean.class, "auth", Maps.objects("token", this.token).build()),
        authenticated -> {
          // TODO show that it was authenticated
        });
    return this.client;
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
      try {
        this.validatedConnection().sendRequest(request, consumer);
      } catch (IOException e) {
        throw new IllegalStateException("There's no connection with the bot", e);
      }
    }
  }

  /**
   * Send a request
   *
   * @param request the request to make
   * @param consumer the method to do after we get the object
   * @param <T> the type of the object
   */
  public <T> void request(@NotNull Request<T> request, @NotNull Consumer<T> consumer) {
    try {
      this.validatedConnection().sendRequest(request, consumer);
    } catch (IOException e) {
      throw new IllegalStateException("There's no connection with the bot", e);
    }
  }

  /**
   * Send a request
   *
   * @param request the request to make
   * @param <T> the type of the object
   * @return the object
   */
  @Nullable
  public <T> T request(@NotNull Request<T> request) {
    try {
      NullableAtomic<T> atomic = new NullableAtomic<>();
      JsonClient connection = this.validatedConnection();
      connection.sendRequest(request, atomic::set);
      int millis = 0;
      while (atomic.get() == null || millis >= connection.getTimeout()) {
        try {
          Thread.sleep(1);
        } catch (InterruptedException e) {
          this.handler.handle(e);
        }
      }
      return atomic.get();
    } catch (IOException e) {
      throw new IllegalStateException("There's no connection with the bot", e);
    }
  }

  /**
   * The validated connection with the server
   *
   * @return the validated connection
   * @throws IOException if the connection could not be stabilised
   */
  @NotNull
  public JsonClient validatedConnection() throws IOException {
    if (this.client != null) {
      return this.client;
    } else {
      return this.startConnection();
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
    return this.client;
  }

  /** Disconnects the client */
  public void disconnect() {
    JsonClient connection = this.getConnection();
    if (connection != null) {
      connection.sendRequest(
          new Request<>(Boolean.class, "disconnect"),
          disconnected -> {
            // TODO show that the client was disconnected
          });
    }
  }

  /**
   * Get the token that the client is using
   *
   * @return the token
   */
  @NotNull
  public String getToken() {
    return this.token;
  }

  /**
   * Set the json client
   *
   * @param client the new value of json client
   */
  public void setConnection(@Nullable JsonClient client) {
    this.client = client;
  }
}
