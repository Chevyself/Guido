package com.starfishst.bungee.core.configuration;

import com.starfishst.bungee.api.configuration.BungeeConfiguration;
import com.starfishst.bungee.api.configuration.GuidoListenerSettings;
import com.starfishst.bungee.api.configuration.GuidoServer;
import com.starfishst.bungee.core.utility.Config;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import me.googas.commons.builder.ToStringBuilder;
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
    this.servers = Config.parseServers(section.getSection("servers"));
    this.settings.addAll(Config.parseSettings(section.getSection("listeners")));
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
    return new ToStringBuilder(this)
        .append("token", this.token)
        .append("guildId", this.guildId)
        .append("servers", this.servers)
        .append("settings", this.settings)
        .build();
  }
}
