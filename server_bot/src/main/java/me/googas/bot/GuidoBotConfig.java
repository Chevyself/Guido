package me.googas.bot;

import lombok.NonNull;

public interface GuidoBotConfig {

  @NonNull
  String getMongoUri();

  @NonNull
  String getDatabase();

  @NonNull
  String getDiscordToken();

  int getServerPort();

  long getTimeout();

  long getGuildId();
}
