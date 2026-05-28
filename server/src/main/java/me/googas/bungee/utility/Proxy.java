package me.googas.bungee.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.NonNull;
import me.googas.bungee.GuidoBungee;
import me.googas.bungee.configuration.GuidoServer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

/** Static methods for the proxy bungee server */
public class Proxy {
  /**
   * Unload all the servers are {@link Config#isSafeToDelete(String, Collection)}
   *
   * @param servers
   */
  public static void unloadServers(List<GuidoServer> servers) {
    List<String> safeToDelete = new ArrayList<>();
    Proxy.instance()
        .getServers()
        .forEach(
            (name, instance) -> {
              if (Config.isSafeToDelete(name, servers)) safeToDelete.add(name);
            });
    safeToDelete.forEach(name -> Proxy.instance().getServers().remove(name));
  }

  public static ServerInfo getServer(@NonNull String ip) {
    for (GuidoServer server : GuidoBungee.getConfiguration().getServers()) {
      GuidoBungee.getLogger().info(ip);
      if (server.getAddress().equalsIgnoreCase(ip)) {
        return ProxyServer.getInstance().getServerInfo(server.getName());
      }
      if (server.getAddress().startsWith("localhost")) {
        if (server.getAddress().substring(9).equalsIgnoreCase(ip)) {
          return ProxyServer.getInstance().getServerInfo(server.getName());
        }
      }
    }
    return null;
  }

  @NonNull
  public static ProxyServer instance() {
    return ProxyServer.getInstance();
  }
}
