package com.starfishst.bungee.core.client.receptors;

import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.api.configuration.GuidoServer;
import com.starfishst.bungee.api.events.GuidoListener;
import java.util.UUID;
import lombok.NonNull;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/** Receptors made for bungee */
// TODO receptors should be separated to different classes
public class BungeeReceptors implements GuidoListener {

  /**
   * Check whether the player with the given id is online the server
   *
   * @param uuid the uuid of the player to check
   * @return true if the player is inside the server
   */
  @Receptor("bungee/is-online")
  public boolean isOnline(@ParamName("uuid") UUID uuid) {
    for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
      if (player.getUniqueId().equals(uuid)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get the name of a server using its ip
   *
   * @param ip the ip of the server to get the name
   * @return the name of the server if the ip matches one else null
   */
  @Receptor("bungee/server-name")
  public String serverName(@ParamName("ip") String ip) {
    for (GuidoServer server : Guido.getConfiguration().getServers()) {
      if (server.getAddress().equalsIgnoreCase(ip)) {
        ServerInfo info = ProxyServer.getInstance().getServerInfo(server.getName());
        if (info != null) {
          return info.getName();
        }
        return null;
      }
    }
    return null;
  }

  @Override
  public void onUnload() {}

  @Override
  public @NonNull String getName() {
    return "receptors";
  }
}
