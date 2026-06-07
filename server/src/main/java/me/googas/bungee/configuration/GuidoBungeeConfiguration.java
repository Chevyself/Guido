package me.googas.bungee.configuration;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import me.googas.bot.GuidoBotConfig;
import me.googas.bungee.utility.Config;
import net.md_5.bungee.config.Configuration;

/** The default yaml configuration for the bot */
public class GuidoBungeeConfiguration implements BungeeConfiguration, GuidoBotConfig {

  @NonNull @Getter private final String botArguments;
  @NonNull @Getter private final String mongoUri;
  @NonNull @Getter private final String database;
  /** The token used for the bot */
  @NonNull @Getter private final String discordToken;

  @Getter private final int serverPort;
  @Getter private final long timeout;

  /** The id of the guild */
  @Getter private final long guildId;

  /** The servers that can be connected using bungee */
  @NonNull private final List<GuidoServer> servers;

  /** The settings that are contained in the configuration */
  @NonNull private final List<GuidoListenerSettings> settings;

  /** Create the bungee configuration */
  public GuidoBungeeConfiguration() {
    this.botArguments = "";
    this.mongoUri = "";
    this.database = "";
    this.discordToken = "0";
    this.serverPort = 0;
    this.timeout = 0;
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

    Configuration mongo = section.getSection("mongo");
    this.mongoUri = mongo.getString("uri", "");
    this.database = mongo.getString("database", "");

    this.discordToken = section.getString("token", "0");

    Configuration server = section.getSection("server");
    this.serverPort = server.getInt("port", 3366);
    this.timeout = server.getLong("timeout", 10000);

    this.settings = new ArrayList<>();
    this.guildId = section.getLong("guild", 0L);
    this.servers = Config.parseServers(section.getSection("servers"));
    this.settings.addAll(Config.parseSettings(section.getSection("listeners")));
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
        + discordToken
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
