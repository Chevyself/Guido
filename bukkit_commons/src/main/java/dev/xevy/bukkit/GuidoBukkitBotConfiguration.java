package dev.xevy.bukkit;

import lombok.Getter;
import lombok.NonNull;

public class GuidoBukkitBotConfiguration {
  @NonNull @Getter private final String mongoUri;
  @NonNull @Getter private final String mongoDatabase;
  @NonNull @Getter private final String discordToken;
  @Getter private final int port;
  @Getter private final long timeout;
  @Getter private final long guildId;

  public GuidoBukkitBotConfiguration(
      @NonNull String mongoUri,
      @NonNull String mongoDatabase,
      @NonNull String discordToken,
      int port,
      long timeout,
      long guildId) {
    this.mongoUri = mongoUri;
    this.mongoDatabase = mongoDatabase;
    this.discordToken = discordToken;
    this.port = port;
    this.timeout = timeout;
    this.guildId = guildId;
  }

  public GuidoBukkitBotConfiguration() {

    this(
        "mongodb://localhost:27017",
        "guido",
        "https://discord.com/developers/",
        3366,
        10000,
        1511402659767128291L);
  }
}
