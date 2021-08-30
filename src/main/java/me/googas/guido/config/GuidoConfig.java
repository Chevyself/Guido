package me.googas.guido.config;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NonNull;
import me.googas.guido.GuidoFiles;

public class GuidoConfig {

  @NonNull @Getter private final String token;
  @NonNull @Getter private final String prefix;
  @NonNull @Getter private final DatabaseConfiguration database;

  @SerializedName("server-port")
  @Getter
  private final int serverPort;

  public GuidoConfig(
      @NonNull String token,
      @NonNull String prefix,
      @NonNull DatabaseConfiguration database,
      int serverPort) {
    this.token = token;
    this.prefix = prefix;
    this.database = database;
    this.serverPort = serverPort;
  }

  public GuidoConfig() {
    this("", "", new DatabaseConfiguration(), 3000);
  }

  public static GuidoConfig load() {
    return GuidoFiles.CONFIG.readOr(
        GuidoFiles.Contexts.JSON, GuidoConfig.class, GuidoFiles.Resources.CONFIG);
  }
}
