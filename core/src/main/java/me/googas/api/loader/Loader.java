package me.googas.api.loader;

import lombok.NonNull;

/** Loads the data. */
public interface Loader {

  @NonNull
  GroupLoader getGroups();

  @NonNull
  TokenLoader getTokens();

  @NonNull
  UserLoader getUsers();
}
