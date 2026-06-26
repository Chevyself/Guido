package me.googas.bungee.data;

import dev.xevy.guido.mc.MinecraftPlayer;
import java.util.UUID;
import lombok.NonNull;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeMinecraftPlayer implements MinecraftPlayer {
  @NonNull private final ProxiedPlayer proxied;

  public BungeeMinecraftPlayer(@NonNull ProxiedPlayer proxied) {
    this.proxied = proxied;
  }

  @Override
  public @NonNull UUID getUniqueId() {
    return proxied.getUniqueId();
  }

  @Override
  public @NonNull String getLocale() {
    return proxied.getLocale().toString().split("_")[0];
  }

  @Override
  public @NonNull String getNickname() {
    return proxied.getName();
  }

  @Override
  public @NonNull String getIp() {
    return proxied.getSocketAddress().toString();
  }
}
