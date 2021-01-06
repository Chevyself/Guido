package me.googas.api.loader;

import lombok.NonNull;

/** Loads the data. */
public interface Loader {

  @NonNull
  GroupLoader getGroups();

  @NonNull
  LinksLoader getLinks();

  @NonNull
  MatchLoader getMatches();

  @NonNull
  PunishmentLoader getPunishments();

  @NonNull
  TeamLoader getTeams();

  @NonNull
  TokenLoader getTokens();

  @NonNull
  UserLoader getUsers();
}
