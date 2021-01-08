package com.starfishst.bungee.core.listeners;

import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.api.events.GuidoListener;
import com.starfishst.bungee.core.data.ProxiedOfflinePlayer;
import com.starfishst.bungee.core.lang.BungeeLocaleFile;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.Requests;
import me.googas.api.punishment.Punishment;
import me.googas.api.punishment.PunishmentStatus;
import me.googas.api.punishment.PunishmentType;
import me.googas.commons.Lots;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.maps.Maps;
import me.googas.messaging.json.client.JsonClient;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;

public class PunishmentsListener implements GuidoListener {

  @EventHandler(priority = ListenPriority.HIGHEST)
  public void onLoginEvent(PostLoginEvent event) {
    ProxiedPlayer player = event.getPlayer();
    JsonClient connection = Guido.getClient().getConnection();
    BungeeLocaleFile locale = Guido.getLanguageHandler().getFile(player);
    if (connection == null && !player.hasPermission("guido.join.maintenance")) {
      player.disconnect(locale.getComponent("server.under-maintenance"));
      return;
    }
    Requests.Punishments.getPunishments(
            new ProxiedOfflinePlayer(event.getPlayer()).getLinkedInfo(),
            Lots.set(PunishmentStatus.ACTIVE))
        .send(
            connection,
            optional -> {
              optional.ifPresent(
                  punishments -> {
                    Punishment active = this.getActive(punishments);
                    if (active == null) return;
                    player.disconnect(
                        locale.getComponent(
                            "server.banned",
                            Maps.singleton(
                                "reason",
                                active
                                    .getDetails()
                                    .getOr("reason", String.class, "No reason provided"))));
                  });
            });
  }

  @Nullable
  private Punishment getActive(@NonNull Punishment... punishments) {
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
