package me.googas.guido.config;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class GuidoConfig {

  @NonNull @Getter private final String host;
  @Getter private final int port;
  @NonNull @Getter private final String token;

  private GuidoConfig(@NonNull String host, int port, @NonNull String token) {
    this.host = host;
    this.port = port;
    this.token = token;
  }

  public static GuidoConfig load(@NonNull Plugin plugin) {
    plugin.saveDefaultConfig();
    FileConfiguration config = plugin.getConfig();
    return new GuidoConfig(
        config.getString("host", "localhost"),
        config.getInt("port", 3000),
        config.getString("token", "dev"));
  }
}
