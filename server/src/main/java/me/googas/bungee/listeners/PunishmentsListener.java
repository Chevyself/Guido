package me.googas.bungee.listeners;

import java.util.Collection;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.API;
import me.googas.api.punishment.Punishment;
import me.googas.api.punishment.PunishmentStatus;
import me.googas.api.punishment.PunishmentType;
import me.googas.bungee.GuidoBungee;
import me.googas.bungee.data.ProxiedOfflinePlayer;
import me.googas.bungee.events.GuidoListener;
import me.googas.bungee.lang.BungeeLocaleFile;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.maps.Maps;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;

public class PunishmentsListener implements GuidoListener {

  @EventHandler(priority = ListenPriority.HIGHEST)
  public void onLoginEvent(PostLoginEvent event) {
    ProxiedPlayer player = event.getPlayer();
    Collection<Punishment> punishments =
        API.getLoader()
            .getPunishments()
            .getPunishments(new ProxiedOfflinePlayer(player).getLink(), PunishmentStatus.ACTIVE);
    Punishment punishment = this.getActive(punishments);
    BungeeLocaleFile locale = GuidoBungee.getLanguageHandler().getFile(player);
    if (punishment != null) {
      player.disconnect(
          locale.getComponent(
              "server.banned",
              Maps.singleton(
                  "reason", punishment.getString(null, "reason", "No reason provided"))));
    }
  }

  @Nullable
  private Punishment getActive(@NonNull Collection<Punishment> punishments) {
    for (Punishment punishment : punishments) {
      if (punishment.getType() == PunishmentType.BAN && !punishment.isExpired()) return punishment;
    }
    return null;
  }

  @Override
  public void onUnload() {}

  @Override
  public @NonNull String getName() {
    return "punishments";
  }
}
