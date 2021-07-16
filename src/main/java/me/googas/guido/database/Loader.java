package me.googas.guido.database;

import lombok.NonNull;
import me.googas.guido.config.DatabaseConfiguration;
import me.googas.guido.config.GuidoConfig;
import me.googas.guido.database.sql.SqlLoader;
import me.googas.net.cache.MemoryCache;

public interface Loader {

  static Loader load(@NonNull GuidoConfig config) {
    DatabaseConfiguration configDatabase = config.getDatabase();
    if (configDatabase.getType() == DatabaseConfiguration.DatabaseType.H2) {
      return new SqlLoader(configDatabase.getUrl());
    }
    throw new IllegalArgumentException("The database type " + configDatabase.getType() + " is not supported yet!");
  }

  @NonNull
  Loader start();

  @NonNull
  Loader close();

  @NonNull
  MembersLoader getMembers();

  @NonNull
  MemoryCache getCache();
}
