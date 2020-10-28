package com.starfishst.bungee.core.client;

import com.starfishst.bungee.api.Guido;
import com.starfishst.guido.api.data.implementations.ClientImpl;
import java.io.IOException;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import me.googas.messaging.json.client.JsonClient;
import org.jetbrains.annotations.NotNull;

/** An extension for client */
public class BungeeClient extends ClientImpl {

  /**
   * Create the client
   *
   * @param token the token
   */
  public BungeeClient(@NotNull String token) {
    super(token);
    this.addReceptors(new BungeeReceptors());
  }

  @Override
  public @NotNull JsonClient startConnection() throws IOException {
    Guido.getLogger().info("Starting connection");
    JsonClient client = super.startConnection();
    client.sendRequest(
        new Request<>(
            Boolean.class, "client-info", Maps.singleton("info", Maps.singleton("bungee", true))),
        saved -> {});
    Guido.getLogger().info("Connection with the bot as been stabilised in " + client);
    return client;
  }
}
