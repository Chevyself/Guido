package com.starfishst.bukkit;

import dev.xevy.bukkit.GuidoBukkitBotConfiguration;
import lombok.NonNull;
import me.googas.bot.GuidoBotConfig;

public class BotConfigProxy implements GuidoBotConfig {
  @NonNull private final GuidoBukkitBotConfiguration configuration;

  public BotConfigProxy(@NonNull GuidoBukkitBotConfiguration configuration) {
    this.configuration = configuration;
  }

  @Override
  public @NonNull String getMongoUri() {
    return configuration.getMongoUri();
  }

  @Override
  public @NonNull String getDatabase() {
    return configuration.getMongoDatabase();
  }

  @Override
  public @NonNull String getDiscordToken() {
    return configuration.getDiscordToken();
  }

  @Override
  public int getServerPort() {
    return configuration.getPort();
  }

  @Override
  public long getTimeout() {
    return configuration.getTimeout();
  }

  @Override
  public long getGuildId() {
    return configuration.getGuildId();
  }
}
