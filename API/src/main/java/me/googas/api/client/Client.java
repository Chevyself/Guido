package me.googas.api.client;

import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.client.adapters.GroupAdapter;
import me.googas.api.client.adapters.LadderAdapter;
import me.googas.api.client.adapters.LinkedInfoAdapter;
import me.googas.api.client.adapters.MatchAdapter;
import me.googas.api.client.adapters.PermissionAdapter;
import me.googas.api.client.adapters.PermissionStackAdapter;
import me.googas.api.client.adapters.TeamAdapter;
import me.googas.api.client.adapters.TeamMemberAdapter;
import me.googas.api.client.adapters.ValuesMapAdapter;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.client.receptors.SimpleReceptors;
import me.googas.api.links.LinkableInfo;
import me.googas.api.matches.Ladder;
import me.googas.api.matches.Match;
import me.googas.api.matches.Team;
import me.googas.api.matches.TeamMember;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.Permission;
import me.googas.api.permissions.PermissionStack;
import me.googas.api.utility.ValuesMap;
import me.googas.commons.Lots;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import me.googas.messaging.api.Message;
import me.googas.messaging.json.adapters.MessageDeserializer;
import me.googas.messaging.json.client.JsonClient;

/** The client used by implementation to connect with Guido */
public class Client {

  /** The ip of the server of the bot */
  @NonNull private final String ip;
  /** The receptors that the client is using */
  @NonNull private final Set<Object> receptors = Lots.set(new SimpleReceptors(this));

  /** The port of the server ofo the bot */
  private final int port;
  /** The handler for throwable */
  @NonNull private final SimpleThrowableHandler handler = new SimpleThrowableHandler(this);
  /** The token that will give access to read or writing */
  @NonNull private String token;
  /** The client to connect with the bot */
  private JsonClient connection;

  /**
   * Create the client
   *
   * @param token the token
   * @param ip the ip of the server of the bot
   * @param port the port of the server of the bot
   */
  public Client(@NonNull String token, @NonNull String ip, int port) {
    this.token = token;
    this.ip = ip;
    this.port = port;
  }

  /**
   * Connects the client with the bot
   *
   * @return the stabilised connection
   * @throws IOException if the bot cannot be reached
   */
  @NonNull
  public JsonClient startConnection() throws IOException {
    this.connection =
        new JsonClient(
            new Socket(this.ip, this.port),
            this.handler,
            new GsonBuilder()
                // Required by messengers
                .registerTypeAdapter(Message.class, new MessageDeserializer())
                // Required for requests
                .registerTypeAdapter(Group.class, new GroupAdapter())
                .registerTypeAdapter(Ladder.class, new LadderAdapter())
                .registerTypeAdapter(LinkableInfo.class, new LinkedInfoAdapter())
                .registerTypeAdapter(Match.class, new MatchAdapter())
                .registerTypeAdapter(Permission.class, new PermissionAdapter())
                .registerTypeAdapter(PermissionStack.class, new PermissionStackAdapter())
                .registerTypeAdapter(Team.class, new TeamAdapter())
                .registerTypeAdapter(TeamMember.class, new TeamMemberAdapter())
                .registerTypeAdapter(ValuesMap.class, new ValuesMapAdapter())
                .registerTypeAdapter(SimpleValuesMap.class, new ValuesMapAdapter())
                .setPrettyPrinting()
                .create(),
            5000);
    this.connection.addReceptors(this.receptors.toArray());
    this.connection.start();
    this.connection.sendRequest(
        new Request<>(Boolean.class, "auth", Maps.objects("token", this.token).build()),
        this::onAuthentication);
    return this.connection;
  }

  /**
   * Called when the client is authenticated
   *
   * @param authenticated whether the client was authenticated properly
   */
  public void onAuthentication(boolean authenticated) {
    System.out.println("Client has been authenticated? " + authenticated);
  }

  /** Called when the client is disconnected */
  public void onDisconnection() {
    if (this.connection != null) this.connection.close();
    this.connection = null;
    System.out.println("Client has been disconnected");
  }

  /**
   * The validated connection with the server
   *
   * @return the validated connection
   * @throws IOException if the connection could not be stabilised
   */
  @NonNull
  public JsonClient validatedConnection() throws IOException {
    if (this.connection != null) {
      return this.connection;
    } else {
      return this.startConnection();
    }
  }

  /**
   * Add all the given receptors to the client
   *
   * @param receptors the receptors to add
   */
  public void addReceptors(@NonNull Object... receptors) {
    this.receptors.addAll(Arrays.asList(receptors));
    JsonClient connection = this.getConnection();
    if (connection != null) connection.addReceptors(receptors);
  }

  /** Disconnects the client */
  public void disconnect() {
    JsonClient connection = this.getConnection();
    if (connection == null) return;
    connection.sendRequest(
        new Request<>(Boolean.class, "disconnect"),
        disconnected -> this.onDisconnection(),
        this.handler::handle);
  }

  /**
   * Set the token that the client should use
   *
   * @param token the new token
   */
  public void setToken(@NonNull String token) {
    this.token = token;
  }

  /**
   * Set the json client
   *
   * @param client the new value of json client
   */
  public void setConnection(JsonClient client) {
    this.connection = client;
  }

  /**
   * Get the json client for messaging
   *
   * @return the json client
   */
  public JsonClient getConnection() {
    return this.connection;
  }

  /**
   * Get the token that the client is using
   *
   * @return the token
   */
  @NonNull
  public String getToken() {
    return this.token;
  }

  /**
   * Get the receptors which the client is using
   *
   * @return the receptors
   */
  @NonNull
  public Set<Object> getReceptors() {
    return this.receptors;
  }
}
