package me.googas.bungee.listeners;

import java.util.HashMap;
import java.util.HashSet;
import lombok.NonNull;
import me.googas.api.API;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.utility.Maps;
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
    String trim = UUIDUtils.trim(connection.getUniqueId());
    LinkableInfo link =
        new LinkableInfo(LinkableType.MINECRAFT, Maps.singleton("uuid", trim), new HashMap<>());
    Linkable linkable =
        API.getLoader().getLinks().getLink(link.getType(), link.getIdentification());
    if (linkable != null) {
      linkable.setRecogString("nickname", nickname);
      linkable.setRecogString("ip", ip);
      linkable.setBoolean(null, "online", true);
    } else {
      new Linkable(
              LinkableType.MINECRAFT,
              link.getIdentification(),
              Maps.objects("nickname", nickname).put("ip", ip).build(),
              new HashMap<>(),
              new HashSet<>(),
              new HashMap<>(),
              new HashMap<>(),
              null)
          .cache();
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerDisconnect(PlayerDisconnectEvent event) {
    String trim = UUIDUtils.trim(event.getPlayer().getUniqueId());
    Linkable linkable =
        API.getLoader().getLinks().getLink(LinkableType.MINECRAFT, Maps.singleton("uuid", trim));
    if (linkable != null) {
      linkable.setBoolean(null, "online", false);
    }
  }

  @Override
  public void onUnload() {}

  @Override
  public @NonNull String getName() {
    return "data";
  }
}
