package me.googas.bungee.data;

import dev.xevy.guido.mc.MinecraftPlayer;
import java.util.UUID;
import lombok.NonNull;
import net.md_5.bungee.api.connection.PendingConnection;

public class PendingConnectionMinecraftPlayer implements MinecraftPlayer {
  @NonNull private final PendingConnection connection;

  public PendingConnectionMinecraftPlayer(@NonNull PendingConnection connection) {
    this.connection = connection;
  }

  @Override
  public @NonNull UUID getUniqueId() {
    return connection.getUniqueId();
  }

  @Override
  public @NonNull String getLocale() {
    throw new UnsupportedOperationException();
  }

  @Override
  public @NonNull String getNickname() {
    return connection.getName();
  }

  @Override
  public @NonNull String getIp() {
    return connection.getSocketAddress().toString();
  }
}
