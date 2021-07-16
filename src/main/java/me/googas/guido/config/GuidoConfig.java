package me.googas.guido.config;

import lombok.Getter;
import lombok.NonNull;
import me.googas.guido.GuidoFiles;

public class GuidoConfig {

  @NonNull @Getter private final String token;
  @NonNull @Getter private final String prefix;
  @NonNull @Getter
  private final DatabaseConfiguration database;

  public GuidoConfig(@NonNull String token, @NonNull String prefix, @NonNull DatabaseConfiguration database) {
    this.token = token;
    this.prefix = prefix;
    this.database = database;
  }

  public GuidoConfig() {
    this("", "", new DatabaseConfiguration());
  }

  public static GuidoConfig load() {
    return GuidoFiles.CONFIG.readOr(
        GuidoFiles.Contexts.JSON, GuidoConfig.class, GuidoFiles.Resources.CONFIG);
  }


}
