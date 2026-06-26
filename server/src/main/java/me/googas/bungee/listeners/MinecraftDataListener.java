package me.googas.bungee.listeners;

import dev.xevy.guido.mc.MinecraftDataSynchronize;
import lombok.NonNull;
import me.googas.bungee.GuidoBungeeRuntime;
import me.googas.bungee.data.BungeeMinecraftPlayer;
import me.googas.bungee.data.PendingConnectionMinecraftPlayer;
import me.googas.bungee.events.GuidoListener;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/** This listener keeps the minecraft data up-to-date in the database */
public class MinecraftDataListener implements GuidoListener {

  @NonNull private final MinecraftDataSynchronize sync = new MinecraftDataSynchronize();
  @NonNull private final GuidoBungeeRuntime runtime;

  public MinecraftDataListener(@NonNull GuidoBungeeRuntime runtime) {
    this.runtime = runtime;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPreLoginEvent(LoginEvent event) {
    sync.onPlayerJoin(
        runtime.getClient(), new PendingConnectionMinecraftPlayer(event.getConnection()));
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerDisconnect(PlayerDisconnectEvent event) {
    sync.onPlayerQuit(runtime.getClient(), new BungeeMinecraftPlayer(event.getPlayer()));
  }

  @Override
  public void onUnload() {}

  @Override
  public @NonNull String getName() {
    return "data";
  }
}
