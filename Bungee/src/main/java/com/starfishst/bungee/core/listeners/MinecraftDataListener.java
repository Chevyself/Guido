package com.starfishst.bungee.core.listeners;

import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.api.events.GuidoListener;
import java.util.HashMap;
import java.util.HashSet;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.client.data.links.SimpleLinkableInfo;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.commons.UUIDUtils;
import me.googas.messaging.json.client.JsonClient;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/** This listener keeps the minecraft data up-to-date in the database */
public class MinecraftDataListener implements GuidoListener {

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPreLoginEvent(LoginEvent event) {
    JsonClient client = Guido.getClient().getConnection();
    PendingConnection connection = event.getConnection();
    String nickname = connection.getName();
    String ip = connection.getSocketAddress().toString();
    String trim = UUIDUtils.trim(connection.getUniqueId());
    LinkableInfo link =
        new SimpleLinkableInfo(LinkableType.MINECRAFT, new SimpleValuesMap("uuid", trim));
    Requests.Links.getLinkByIdentification(link.getType(), link.getIdentification())
        .send(
            client,
            Requests.ifPresentElse(
                linkable -> {
                  // Update link and ip
                  Requests.Links.setRecognition(link, "nickname", nickname).queue(client);
                  Requests.Links.setRecognition(link, "ip", ip).queue(client);
                  Requests.Links.preference(link, "online", true).queue(client);
                  // TODO this means that it exists maybe it can be used in the future
                },
                () ->
                    Requests.Links.create(
                            link.getType(),
                            link.getIdentification(),
                            new SimpleValuesMap("nickname", nickname).put("ip", ip),
                            new SimpleValuesMap("online", true),
                            new HashMap<>(),
                            new HashSet<>())
                        .queue(client)));
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerDisconnect(PlayerDisconnectEvent event) {
    JsonClient client = Guido.getClient().getConnection();
    if (client == null) return;
    String trim = UUIDUtils.trim(event.getPlayer().getUniqueId());
    LinkableInfo link =
        new SimpleLinkableInfo(LinkableType.MINECRAFT, new SimpleValuesMap("uuid", trim));
    Requests.Links.removePreference(link, "online").queue(client);
  }

  @Override
  public void onUnload() {}

  @Override
  public @NonNull String getName() {
    return "data";
  }
}
