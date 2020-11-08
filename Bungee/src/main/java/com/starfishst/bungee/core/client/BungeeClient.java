package com.starfishst.bungee.core.client;

import com.starfishst.bungee.api.Guido;
import java.io.IOException;
import me.googas.api.client.Client;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import me.googas.messaging.json.client.JsonClient;
import org.jetbrains.annotations.NotNull;

/** An extension for client */
public class BungeeClient extends Client {

  /**
   * Create the client
   *
   * @param token the token
   */
  public BungeeClient(@NotNull String token) {
    super(token, "104.243.43.175", 3000);
    this.addReceptors(new BungeeReceptors());
  }

  @Override
  public @NotNull JsonClient startConnection() throws IOException {
    Guido.getLogger().info("Starting connection");
    JsonClient client = super.startConnection();
    Guido.getLogger().info("Connection with the bot as been stabilised in " + client);
    return client;
  }

  /**
   * Called when the client is authenticated
   *
   * @param authenticated whether the client was authenticated properly
   */
  @Override
  public void onAuthentication(boolean authenticated) {
    JsonClient connection = this.getConnection();
    if (authenticated && connection != null) {
      connection.sendRequest(
          new Request<>(
              Boolean.class, "client-info", Maps.singleton("info", Maps.singleton("bungee", true))),
          saved -> {});
    }
  }
}
