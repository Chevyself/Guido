package com.starfishst.bungee.core.utility;

import com.starfishst.bungee.api.configuration.GuidoServer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.NonNull;
import net.md_5.bungee.api.ProxyServer;

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

  @NonNull
  public static ProxyServer instance() {
    return ProxyServer.getInstance();
  }
}
