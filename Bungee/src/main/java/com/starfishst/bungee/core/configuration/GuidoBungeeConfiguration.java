package com.starfishst.bungee.core.configuration;

import com.starfishst.bungee.api.configuration.BungeeConfiguration;
import com.starfishst.bungee.api.configuration.GuidoListenerSettings;
import com.starfishst.bungee.api.configuration.GuidoServer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.NonNull;
import net.md_5.bungee.config.Configuration;

/** The default yaml configuration for the bot */
public class GuidoBungeeConfiguration implements BungeeConfiguration {

  /** The token used for the bot */
  @NonNull private final String token;

  /** The id of the guild */
  private final long guildId;

  /** The servers that can be connected using bungee */
  @NonNull private final List<GuidoServer> servers;

  /** The settings that are contained in the configuration */
  @NonNull private final List<GuidoListenerSettings> settings;

  /** Create the bungee configuration */
  public GuidoBungeeConfiguration() {
    this.token = "0";
    this.guildId = 0;
    this.servers = new ArrayList<>();
    this.settings = new ArrayList<>();
  }

  /**
   * Create the bungee configuration
   *
   * @param section the section
   */
  public GuidoBungeeConfiguration(@NonNull Configuration section) {
    this.settings = new ArrayList<>();
    this.token = section.getString("token", "0");
    this.guildId = section.getLong("guild", 0L);
    this.servers = new ArrayList<>();
    if (section.get("servers") != null) {
      Configuration serversSection = section.getSection("servers");
      for (String name : serversSection.getKeys()) {
        GuidoServerImpl server =
            new GuidoServerImpl(
                name,
                serversSection.get(name + ".address", "localhost"),
                serversSection.getBoolean(name + ".restricted", false));
        this.servers.add(server);
      }
    }
    if (section.get("listeners") != null) {
      Configuration settingsSection = section.getSection("listeners");
      for (String name : settingsSection.getKeys()) {
        // TODO a proper class for this
        HashMap<String, Object> settings = new HashMap<>();
        Configuration nameSection = settingsSection.getSection(name);
        for (String key : nameSection.getKeys()) {
          settings.put(key, nameSection.get(key));
        }
        this.settings.add(
            new GuidoListenerSettings() {
              @Override
              public @NonNull String getName() {
                return name;
              }

              @Override
              public @NonNull HashMap<String, Object> getSettings() {
                return settings;
              }
            });
      }
    }
  }

  @NonNull
  @Override
  public String getToken() {
    return this.token;
  }

  @Override
  public @NonNull List<GuidoServer> getServers() {
    return this.servers;
  }

  @Override
  public @NonNull List<GuidoListenerSettings> getListenersSettings() {
    return this.settings;
  }

  @Override
  public String toString() {
    return "GuidoBungeeConfiguration{"
        + "token='"
        + this.token
        + '\''
        + ", guildId="
        + this.guildId
        + ", servers="
        + this.servers
        + ", settings="
        + this.settings
        + '}';
  }
}
