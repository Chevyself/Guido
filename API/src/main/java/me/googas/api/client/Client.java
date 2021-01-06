package me.googas.api.client;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Delegate;
import me.googas.annotations.Nullable;
import me.googas.api.client.receptors.SimpleReceptors;
import me.googas.api.utility.Adapters;
import me.googas.commons.Lots;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import me.googas.messaging.json.client.JsonClient;

/** The client used by implementation to connect with Guido */
public class Client {

  /** An static instance of client to use */
  @Nullable @Getter @Setter private static Client instance;

  /** The receptors that the client is using */
  @NonNull @Setter private Set<Object> receptors = Lots.set(new SimpleReceptors(this));

  /** The ip of the server of the bot */
  @NonNull @Getter @Setter private String ip;

  /**
   * Get the validated connection from {@link #validatedConnection()} but if it throws the
   * IOException it will throw a null pointer
   *
   * @return the validated connection
   * @throws NullPointerException if there's no connection and no connection could be stabilised
   */
  @NonNull
  @Delegate(excludes = ClientMethods.class)
  public JsonClient validated() {
    try {
      return this.validatedConnection();
    } catch (IOException e) {
      throw new NullPointerException("Could not validate connection with Guido-Bot!");
    }
  }

  /** The port of the server ofo the bot */
  @Getter @Setter private int port;
  /** The handler for throwable */
  @NonNull @Getter @Setter
  private SimpleThrowableHandler handler = new SimpleThrowableHandler(this);
  /** The token that will give access to read or writing */
  @NonNull @Getter @Setter private String token;
  /** The client to connect with the bot */
  @Getter @Setter private JsonClient connection;

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
        new JsonClient(new Socket(this.ip, this.port), this.handler, Adapters.buildClient(), 5000);
    this.connection.addReceptors(this.receptors.toArray());
    this.connection.start();
    this.connection.sendRequest(
        new Request<>(Boolean.class, "auth", Maps.objects("token", this.token).build()),
        bol -> {
          if (bol.isPresent()) {
            this.onAuthentication(bol.get());
          } else {
            this.onAuthentication(false);
          }
        });
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

  @SuppressWarnings("unused")
  public interface ClientMethods {
    void addReceptors(Object... receptors);
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
}
