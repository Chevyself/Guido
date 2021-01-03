package com.starfishst.bungee.core.client;

import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.api.events.GuidoListener;
import com.starfishst.bungee.core.client.receptors.BungeeConnectionReceptors;
import com.starfishst.bungee.core.client.receptors.BungeeMessagingReceptors;
import com.starfishst.bungee.core.client.receptors.BungeeQueueReceptors;
import com.starfishst.bungee.core.client.receptors.BungeeReceptors;
import com.starfishst.bungee.core.listeners.GroupListener;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import me.googas.api.client.Client;
import me.googas.commons.Lots;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import me.googas.messaging.json.client.JsonClient;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

/** An extension for client */
public class BungeeClient extends Client {

  /**
   * Create the client
   *
   * @param token the token
   */
  public BungeeClient(@NonNull String token, @NonNull Plugin plugin) {
    super(token, "167.114.49.251", 3000);
    for (GuidoListener receptor : Lots.set(new BungeeReceptors())) {
      this.addReceptors(new BungeeConnectionReceptors());
      this.addReceptors(new BungeeMessagingReceptors());
      this.addReceptors(new BungeeQueueReceptors());
      this.addReceptors(new BungeeReceptors());
      plugin.getProxy().getPluginManager().registerListener(plugin, receptor);
    }
  }

  @Override
  public @NonNull JsonClient startConnection() throws IOException {
    Guido.getLogger().info("Starting connection");
    ProxyServer.getInstance()
        .getScheduler()
        .schedule(Guido.validated(), new BungeeHeartBeatTimerTask(this), 10, 10, TimeUnit.SECONDS);
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
      Guido.getListener(GroupListener.class).loadGroups(null);
    }
  }
}
