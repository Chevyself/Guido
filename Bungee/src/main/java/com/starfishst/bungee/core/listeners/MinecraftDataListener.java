package com.starfishst.bungee.core.listeners;

import com.starfishst.bungee.api.events.GuidoListener;
import com.starfishst.bungee.core.client.requests.BungeeBooleanRequest;
import com.starfishst.bungee.core.client.requests.BungeeRequest;
import java.util.HashMap;
import java.util.HashSet;
import lombok.NonNull;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.client.data.links.SimpleLinkableInfo;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.commons.UUIDUtils;
import me.googas.commons.maps.MapBuilder;
import me.googas.commons.maps.Maps;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/** This listener keeps the minecraft data up-to-date in the database */
public class MinecraftDataListener implements GuidoListener {

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPreLoginEvent(PreLoginEvent event) {
    PendingConnection connection = event.getConnection();
    String ip = connection.getVirtualHost().getHostName();
    String trim = UUIDUtils.trim(connection.getUniqueId());
    LinkableInfo link =
        new SimpleLinkableInfo(LinkableType.MINECRAFT, new SimpleValuesMap("uuid", trim));
    MapBuilder<String, Object> builder = Maps.objects("link", link);
    new BungeeBooleanRequest("link/exists", builder)
        .send(
            exists -> {
              if (exists) {
                new BungeeBooleanRequest(
                        "minecraft/nickname", builder.append("nickname", connection.getName()))
                    .queue();
                new BungeeBooleanRequest("minecraft/ip", builder.append("ip", ip)).queue();
              } else {
                new BungeeRequest<>(
                        Linkable.class,
                        "link/create",
                        Maps.objects("type", LinkableType.MINECRAFT)
                            .append(
                                "recognition",
                                new SimpleValuesMap("nickname", connection.getName()).put("ip", ip))
                            .append("identification", new SimpleValuesMap("uuid", trim))
                            .append("preferences", new SimpleValuesMap())
                            .append("stats", new HashMap<>())
                            .append("permissions", new HashSet<>()))
                    .queue();
              }
            });
  }

  @Override
  public void onUnload() {}

  @Override
  public @NonNull String getName() {
    return "data";
  }
}
