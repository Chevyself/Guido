package me.googas.bot.core.loader;

import lombok.NonNull;
import me.googas.api.loader.BanksLoader;
import me.googas.api.loader.GroupLoader;
import me.googas.api.loader.LadderLoader;
import me.googas.api.loader.LinksLoader;
import me.googas.api.loader.MatchLoader;
import me.googas.api.loader.PunishmentLoader;
import me.googas.api.loader.TeamLoader;
import me.googas.api.loader.TokenLoader;
import me.googas.api.loader.UserLoader;

/**
 * This loader will attempt to getId the data from files if it fails it will create a new instance
 * of the data required
 */
public class GuidoFallbackLoader implements GuidoLoader {
  @Override
  public void onDisable() {}

  @Override
  public @NonNull BanksLoader getBanks() {
    throw new UnsupportedOperationException("Operations are not supported by fallback loader");
  }

  @Override
  public @NonNull LadderLoader getLadders() {
    throw new UnsupportedOperationException("Operations are not supported by fallback loader");
  }

  @Override
  public @NonNull GroupLoader getGroups() {
    throw new UnsupportedOperationException("Operations are not supported by fallback loader");
  }

  @Override
  public @NonNull LinksLoader getLinks() {
    throw new UnsupportedOperationException("Operations are not supported by fallback loader");
  }

  @Override
  public @NonNull MatchLoader getMatches() {
    throw new UnsupportedOperationException("Operations are not supported by fallback loader");
  }

  @Override
  public @NonNull PunishmentLoader getPunishments() {
    throw new UnsupportedOperationException("Operations are not supported by fallback loader");
  }

  @Override
  public @NonNull TeamLoader getTeams() {
    throw new UnsupportedOperationException("Operations are not supported by fallback loader");
  }

  @Override
  public @NonNull TokenLoader getTokens() {
    throw new UnsupportedOperationException("Operations are not supported by fallback loader");
  }

  @Override
  public @NonNull UserLoader getUsers() {
    throw new UnsupportedOperationException("Operations are not supported by fallback loader");
  }
}
