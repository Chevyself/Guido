package com.starfishst.bukkit.modules;

import com.starfishst.bukkit.BukkitMinecraftPlayer;
import dev.xevy.bukkit.AbstractGuidoModule;
import dev.xevy.bukkit.GuidoBukkitRuntime;
import dev.xevy.guido.mc.MinecraftDataSynchronize;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MinecraftDataListener extends AbstractGuidoModule {
  @NonNull private final MinecraftDataSynchronize sync = new MinecraftDataSynchronize();

  public MinecraftDataListener(@NonNull GuidoBukkitRuntime runtime) {
    super(runtime);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerJoin(PlayerJoinEvent event) {
    runtime
        .getClient()
        .getConnection()
        .ifPresent(
            connection -> {
              sync.onPlayerJoin(connection, new BukkitMinecraftPlayer(event.getPlayer()));
            });
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerQuit(PlayerQuitEvent event) {
    runtime
        .getClient()
        .getConnection()
        .ifPresent(
            connection -> {
              sync.onPlayerQuit(connection, new BukkitMinecraftPlayer(event.getPlayer()));
            });
  }

  @Override
  public @NonNull String getName() {
    return "data";
  }
}
