package me.googas.bungee.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.NonNull;
import me.googas.bungee.configuration.GuidoListenerSettings;
import me.googas.bungee.configuration.GuidoServer;
import me.googas.bungee.configuration.SimpleGuidoListenerSettings;
import me.googas.bungee.configuration.SimpleGuidoServer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;

/** Static utilities for configuration */
public class Config {

  @NonNull
  public static List<GuidoServer> parseServers(Configuration config) {
    List<GuidoServer> servers = new ArrayList<>();
    if (config == null) return servers;
    for (String name : config.getKeys()) {
      SimpleGuidoServer server =
          new SimpleGuidoServer(
              name,
              config.get(name + ".address", "localhost"),
              config.getBoolean(name + ".restricted", false));
      servers.add(server);
    }
    return servers;
  }

  @NonNull
  public static Set<GuidoListenerSettings> parseSettings(Configuration config) {
    Set<GuidoListenerSettings> parsedSettings = new HashSet<>();
    if (config == null) return parsedSettings;
    for (String name : config.getKeys()) {
      HashMap<String, Object> settings = new HashMap<>();
      Configuration settingsSection = config.getSection(name);
      for (String key : settingsSection.getKeys()) {
        settings.put(key, settingsSection.get(key));
      }
      parsedSettings.add(new SimpleGuidoListenerSettings(name, settings));
    }
    return parsedSettings;
  }

  public static boolean isSafeToDelete(
      @NonNull String serverName, @NonNull Collection<GuidoServer> servers) {
    for (GuidoServer server : servers) {
      if (server.getName().equalsIgnoreCase(serverName)) return false;
    }
    for (String key : ProxyServer.getInstance().getConfig().getServers().keySet()) {
      if (key.equalsIgnoreCase(serverName)) return false;
    }
    return true;
  }
}
