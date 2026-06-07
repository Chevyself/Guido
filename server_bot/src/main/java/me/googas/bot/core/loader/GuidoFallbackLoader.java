package me.googas.bot.core.loader;

import lombok.NonNull;
import me.googas.api.loader.*;

/**
 * This loader will attempt to getId the data from files if it fails it will create a new instance
 * of the data required
 */
public class GuidoFallbackLoader implements me.googas.server.loader.GuidoLoader {
  @Override
  public @NonNull TokenLoader getTokens() {
    throw new UnsupportedOperationException("Operations are not supported by fallback loader");
  }

  @Override
  public @NonNull UserLoader getUsers() {
    throw new UnsupportedOperationException("Operations are not supported by fallback loader");
  }

  @Override
  public @NonNull StatsLoader getStats() {
    throw new UnsupportedOperationException("Operations are not supported by fallback loader");
  }

  @Override
  public @NonNull MinecraftMatchLoader getMinecraftMatches() {
    throw new UnsupportedOperationException("Operations are not supported by fallback loader");
  }

  @Override
  public @NonNull MinecraftLinkableLoader getMinecraftLinks() {
    throw new UnsupportedOperationException("Operations are not supported by fallback loader");
  }

  @Override
  public @NonNull DiscordLinkableLoader getDiscordLinks() {
    throw new UnsupportedOperationException("Operations are not supported by fallback loader");
  }

  @Override
  public @NonNull me.googas.server.loader.GuidoGuildLoader getGuidoGuildLoader() {
    throw new UnsupportedOperationException("Operations are not supported by fallback loader");
  }
}
