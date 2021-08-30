package me.googas.guido.config;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NonNull;
import me.googas.guido.GuidoFiles;
import me.googas.io.StarboxFile;
import me.googas.io.context.Json;

public class GuidoConfig {

  @NonNull @Getter private final String token;
  @NonNull @Getter private final String prefix;
  @NonNull @Getter private final DatabaseConfiguration database;

  @NonNull
  @Getter
  @SerializedName("server-token")
  private final String serverToken;

  @SerializedName("server-port")
  @Getter
  private final int serverPort;

  public GuidoConfig(
      @NonNull String token,
      @NonNull String prefix,
      @NonNull DatabaseConfiguration database,
      @NonNull String serverToken,
      int serverPort) {
    this.token = token;
    this.prefix = prefix;
    this.database = database;
    this.serverToken = serverToken;
    this.serverPort = serverPort;
  }

  private GuidoConfig() {
    this("", ".", new DatabaseConfiguration(), "dev", 3000);
  }

  public static GuidoConfig load() {
    Json json = GuidoFiles.Contexts.JSON;
    StarboxFile configFile = GuidoFiles.CONFIG;
    if (configFile.exists()) {
      return configFile
          .read(json, GuidoConfig.class)
          .provide()
          .orElseThrow(() -> new IllegalStateException("Could not load configuration"));
    } else {
      json.write(configFile, new GuidoConfig()).run();
      return GuidoConfig.load();
    }
  }
}
