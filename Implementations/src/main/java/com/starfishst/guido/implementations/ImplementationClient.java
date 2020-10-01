package com.starfishst.guido.implementations;

import com.starfishst.guido.api.implementations.messaging.json.JsonClient;
import com.starfishst.guido.api.implementations.messaging.json.requests.AuthenticationRequest;
import com.starfishst.guido.implementations.response.DisconnectedResponse;
import java.io.IOException;
import java.net.Socket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The client used by implementation to connect with Guido */
public class ImplementationClient {

  /** The ip of the bot */
  @NotNull private static final String ip = "localhost";

  /** The port of the bot */
  private static final int port = 3000;
  /** The token that will give access to read or writing */
  @NotNull private final String token;
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
    client = new JsonClient(new Socket(ip, port), 1000);
    client.getResponseGivers().put("disconnected", new DisconnectedResponse(this));
    client.start();
    client.sendRequest(new AuthenticationRequest(this.token));
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
   * Get the json client for messaging
   *
   * @return the json client
   */
  @Nullable
  public JsonClient getClient() {
    return client;
  }
}
