package com.starfishst.bukkit;

import dev.xevy.guido.mc.MinecraftPlayer;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.entity.Player;

public class BukkitMinecraftPlayer implements MinecraftPlayer {
  @NonNull private final Player player;

  public BukkitMinecraftPlayer(@NonNull Player player) {
    this.player = player;
  }

  @Override
  public @NonNull UUID getUniqueId() {
    return player.getUniqueId();
  }

  @Override
  public @NonNull String getLocale() {
    return player.spigot().getLocale().split("_")[0];
  }

  @Override
  public @NonNull String getNickname() {
    return player.getName();
  }

  @Override
  public @NonNull String getIp() {
    return player.getAddress().toString();
  }
}
