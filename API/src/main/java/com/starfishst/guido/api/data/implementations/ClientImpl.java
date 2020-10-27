package com.starfishst.guido.api.data.implementations;

import com.google.gson.GsonBuilder;
import com.starfishst.guido.api.data.Group;
import com.starfishst.guido.api.data.Permission;
import com.starfishst.guido.api.data.PermissionStack;
import com.starfishst.guido.api.data.ValuesMap;
import com.starfishst.guido.api.data.implementations.data.ValuesMapImpl;
import com.starfishst.guido.api.data.implementations.data.adapters.GroupDeserializer;
import com.starfishst.guido.api.data.implementations.data.adapters.LadderDeserializer;
import com.starfishst.guido.api.data.implementations.data.adapters.LinkedInfoDeserializer;
import com.starfishst.guido.api.data.implementations.data.adapters.MatchDeserializer;
import com.starfishst.guido.api.data.implementations.data.adapters.PermissionAdapter;
import com.starfishst.guido.api.data.implementations.data.adapters.PermissionStackDeserializer;
import com.starfishst.guido.api.data.implementations.data.adapters.TeamDeserializer;
import com.starfishst.guido.api.data.implementations.data.adapters.TeamMemberDeserializer;
import com.starfishst.guido.api.data.implementations.data.adapters.ValuesMapAdapter;
import com.starfishst.guido.api.data.implementations.receptors.ReceptorsImpl;
import com.starfishst.guido.api.data.links.LinkedInfo;
import com.starfishst.guido.api.data.matches.Ladder;
import com.starfishst.guido.api.data.matches.Match;
import com.starfishst.guido.api.data.matches.Team;
import com.starfishst.guido.api.data.matches.TeamMember;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import me.googas.commons.Lots;
import me.googas.commons.cache.Cache;
import me.googas.commons.cache.ICatchable;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import me.googas.messaging.api.Message;
import me.googas.messaging.api.MessengerListenFailException;
import me.googas.messaging.json.adapters.MessageDeserializer;
import me.googas.messaging.json.client.JsonClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The client used by implementation to connect with Guido */
public class ClientImpl {

  /** The ip of the bot */
  @NotNull private static final String IP = "104.243.43.175";

  /** The ip of the bot in case of debug */
  @Deprecated @NotNull private static final String DEBUG_IP = "localhost";

  /** The port of the bot */
  private static final int PORT = 3000;

  /** The token that will give access to read or writing */
  @NotNull private String token;
  /** The client to connect with the bot */
  @Nullable private JsonClient client;
  /** The receptors that the client is using */
  @NotNull private final Set<Object> receptors = Lots.set(new ReceptorsImpl(this));
  /** The handler for throwable */
  @NotNull private final ThrowableHandlerImpl handler = new ThrowableHandlerImpl(this);

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
            this.handler,
            new GsonBuilder()
                .registerTypeAdapter(Group.class, new GroupDeserializer())
                .registerTypeAdapter(Ladder.class, new LadderDeserializer())
                .registerTypeAdapter(LinkedInfo.class, new LinkedInfoDeserializer())
                .registerTypeAdapter(Match.class, new MatchDeserializer())
                .registerTypeAdapter(Permission.class, new PermissionAdapter())
                .registerTypeAdapter(PermissionStack.class, new PermissionStackDeserializer())
                .registerTypeAdapter(Team.class, new TeamDeserializer())
                .registerTypeAdapter(TeamMember.class, new TeamMemberDeserializer())
                .registerTypeAdapter(ValuesMap.class, new ValuesMapAdapter())
                .registerTypeAdapter(ValuesMapImpl.class, new ValuesMapAdapter())
                .registerTypeAdapter(Message.class, new MessageDeserializer())
                .setPrettyPrinting()
                .create(),
            5000);
    this.client.addReceptors(this.receptors.toArray());
    this.client.start();
    this.client.sendRequest(
        new Request<>(Boolean.class, "auth", Maps.objects("token", this.token).build()),
        authenticated -> {
          // TODO show that it was authenticated
        });
    return this.client;
  }

  /**
   * @see JsonClient#sendRequest(Request, Consumer) this method is delegated but it also allows to
   *     get an object from cache
   * @param request the request to make
   * @param predicate the method to get the object from the cache
   * @param consumer the method to execute with the given object
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
   * @see JsonClient#sendRequest(Request, Consumer) this method is delegated
   * @param request the request to send
   * @param consumer the consumer of the request
   * @param <T> the type of the object requested
   */
  public <T> void request(@NotNull Request<T> request, @NotNull Consumer<T> consumer) {
    try {
      this.validatedConnection().sendRequest(request, consumer);
    } catch (IOException e) {
      throw new IllegalStateException("There's no connection with the bot", e);
    }
  }

  /**
   * @see JsonClient#sendRequest(Request) this method is delegated
   * @param request the request to send
   * @param <T> the type of object requested
   * @return the object requested
   * @throws MessengerListenFailException if the connection times out
   */
  @Nullable
  public <T> T request(@NotNull Request<T> request) throws MessengerListenFailException {
    try {
      return this.validatedConnection().sendRequest(request);
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

  /**
   * Add all the given receptors to the client
   *
   * @param receptors the receptors to add
   */
  public void addReceptors(@NotNull Object... receptors) {
    this.receptors.addAll(Arrays.asList(receptors));
    JsonClient connection = this.getConnection();
    if (connection != null) {
      connection.addReceptors(receptors);
    }
  }
}
