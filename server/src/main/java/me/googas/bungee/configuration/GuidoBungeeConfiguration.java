package me.googas.bungee.configuration;

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import me.googas.bungee.utility.Config;
import net.md_5.bungee.config.Configuration;

/** The default yaml configuration for the bot */
public class GuidoBungeeConfiguration implements BungeeConfiguration {

  @NonNull private final String botArguments;
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
    this.botArguments = "";
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
    this.botArguments = section.getString("arguments", "");
    this.settings = new ArrayList<>();
    this.token = section.getString("token", "0");
    this.guildId = section.getLong("guild", 0L);
    this.servers = Config.parseServers(section.getSection("servers"));
    this.settings.addAll(Config.parseSettings(section.getSection("listeners")));
  }

  @Override
  public @NonNull String getBotArguments() {
    return this.botArguments;
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
        + "botArguments='"
        + botArguments
        + '\''
        + ", token='"
        + token
        + '\''
        + ", guildId="
        + guildId
        + ", servers="
        + servers
        + ", settings="
        + settings
        + '}';
  }
}
