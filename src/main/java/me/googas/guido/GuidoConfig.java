package me.googas.guido;

import lombok.Getter;
import lombok.NonNull;

public class GuidoConfig {

  @NonNull @Getter private final String token;
  @NonNull @Getter private final String prefix;

  public GuidoConfig(@NonNull String token, @NonNull String prefix) {
    this.token = token;
    this.prefix = prefix;
  }

  public GuidoConfig() {
    this("", "");
  }

  public static GuidoConfig load() {
    return GuidoFiles.CONFIG.readOr(
        GuidoFiles.Contexts.JSON, GuidoConfig.class, GuidoFiles.Resources.CONFIG);
  }
}
