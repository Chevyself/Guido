package me.googas.api.loader;

import lombok.NonNull;

/** Loads the data. */
public interface Loader {

  @NonNull
  TokenLoader getTokens();

  @NonNull
  UserLoader getUsers();

  @NonNull
  StatsLoader getStats();

  @NonNull
  MinecraftMatchLoader getMinecraftMatches();

  @NonNull
  MinecraftLinkableLoader getMinecraftLinks();

  @NonNull
  DiscordLinkableLoader getDiscordLinks();
}
