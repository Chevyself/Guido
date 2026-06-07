package me.googas.bungee.listeners;

import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.bungee.events.GuidoListener;
import me.googas.starbox.UUIDUtils;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/** This listener keeps the minecraft data up-to-date in the database */
public class MinecraftDataListener implements GuidoListener {

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPreLoginEvent(LoginEvent event) {
    PendingConnection connection = event.getConnection();
    String nickname = connection.getName();
    String ip = connection.getSocketAddress().toString();

    Requests.MinecraftLinks.updateStatus(connection.getUniqueId(), nickname, ip, true);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerDisconnect(PlayerDisconnectEvent event) {
    String trim = UUIDUtils.trim(event.getPlayer().getUniqueId());
    Requests.MinecraftLinks.updateOnline(event.getPlayer().getUniqueId(), false);
  }

  @Override
  public void onUnload() {}

  @Override
  public @NonNull String getName() {
    return "data";
  }
}
