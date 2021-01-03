package com.starfishst.bungee.core.utility;

import com.starfishst.bungee.api.configuration.GuidoListenerSettings;
import com.starfishst.bungee.api.configuration.GuidoServer;
import com.starfishst.bungee.core.configuration.SimpleGuidoListenerSettings;
import com.starfishst.bungee.core.configuration.SimpleGuidoServer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import net.md_5.bungee.config.Configuration;

/** Static utilities for configuration */
public class Config {

  @NonNull
  public static List<GuidoServer> parseServers(@Nullable Configuration config) {
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
  public static Set<GuidoListenerSettings> parseSettings(@Nullable Configuration config) {
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
}
