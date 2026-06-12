package me.googas.api.client;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.api.Requests;
import me.googas.api.client.receptors.SimpleReceptors;
import me.googas.api.utility.Adapters;
import me.googas.net.sockets.json.client.JsonClient;

/** The client used by implementation to connect with Guido */
public class Client {

  @NonNull private static final Logger logger = Logger.getLogger(Client.class.getName());

  /** An static instance of client to use */
  @Getter @Setter private static Client instance;

  /** The receptors that the client is using */
  @NonNull @Setter
  private Set<Object> receptors =
      new HashSet<>(Collections.singletonList(new SimpleReceptors(this)));

  /** The ip of the server of the bot */
  @NonNull @Getter @Setter private String ip;

  /** The port of the server ofo the bot */
  @Getter @Setter private int port;
  /** The token that will give access to read or writing */
  @NonNull @Getter @Setter private String token;

  private final long timeout;
  /** The client to connect with the bot */
  @Setter private JsonClient connection;

  /**
   * Create the client
   *
   * @param token the token
   * @param ip the ip of the server of the bot
   * @param port the port of the server of the bot
   */
  public Client(@NonNull String token, @NonNull String ip, int port, long timeout) {
    this.token = token;
    this.ip = ip;
    this.port = port;
    this.timeout = timeout;
  }

  private void connect() throws IOException {
    if (this.connection != null && !this.connection.isClosed()) {
      this.connection.close();
      this.connection = null;
    }
    this.connection =
        JsonClient.join(this.ip, this.port)
            .setGson(Adapters.buildClient())
            .addReceptors(this.receptors.toArray())
            .handle(
                e -> {
                  logger.log(Level.SEVERE, "Error on client", e);
                })
            .maxWait(this.timeout)
            .start();
  }

  /**
   * Connects the client with the bot
   *
   * @return the future with the auth result
   * @throws IOException if the bot cannot be reached
   */
  @NonNull
  public CompletableFuture<Boolean> startConnection() throws IOException {
    connect();
    return Requests.Server.auth(this.token)
        .future(this.connection)
        .whenComplete(
            ((result, ignoredException) -> {
              if (result == null || ignoredException != null) {
                this.onAuthentication(false);
                return;
              }
              this.onAuthentication(result);
            }));
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

  @SuppressWarnings("unused")
  public interface ClientMethods {
    void addReceptors(Object... receptors);
  }

  public Optional<JsonClient> getConnection() {
    if (this.connection != null && this.connection.isClosed()) {
      this.connection = null;
    }
    return Optional.ofNullable(this.connection);
  }

  /**
   * Add all the given receptors to the client
   *
   * @param receptors the receptors to add
   */
  public void addReceptors(@NonNull Object... receptors) {
    this.receptors.addAll(Arrays.asList(receptors));
    try {
      this.connect();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** Disconnects the client */
  public void disconnect() {
    this.getConnection()
        .ifPresent(
            connection -> {
              Requests.Server.disconnect().future(connection);
            });
  }
}
