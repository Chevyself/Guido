package com.starfishst.bungee.core;

import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.api.configuration.GuidoServer;
import com.starfishst.bungee.api.events.GuidoListener;
import com.starfishst.guido.api.data.links.LinkedDataType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import me.googas.commons.UUIDUtils;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Receptors made for bungee */
public class BungeeReceptors implements GuidoListener {

  /** The uuids of the player in queue */
  @NotNull private final Set<UUID> inQueue = new HashSet<>();

  @Receptor(method = "is-online")
  public boolean isOnline(@ParamName(name = "uuids") UUID uuid) {
    for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
      if (player.getUniqueId().equals(uuid)) {
        return true;
      }
    }
    return false;
  }

  @Receptor(method = "add-queue")
  public boolean addQueue(@ParamName(name = "uuid") UUID uuid) {
    return this.inQueue.add(uuid);
  }

  @Receptor(method = "remove-queue")
  public boolean removeQueue(@ParamName(name = "uuid") UUID uuid) {
    return this.inQueue.remove(uuid);
  }

  @Receptor(method = "send-to-server")
  public boolean sendToServer(
      @ParamName(name = "uuid") UUID uuid, @ParamName(name = "server") String server) {
    ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server);
    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
    if (player != null && serverInfo != null) {
      if (!player.getServer().getInfo().equals(serverInfo)) {
        player.connect(serverInfo);
      }
      return true;
    }
    return false;
  }

  @Receptor(method = "send-to-server-by-ip")
  public boolean sendToServerbyIp(
      @ParamName(name = "uuids") List<String> uuidsStrings, @ParamName(name = "server") String ip) {
    List<UUID> uuids = new ArrayList<>();
    for (String string : uuidsStrings) {
      try {
        uuids.add(UUID.fromString(string));
      } catch (IllegalArgumentException ignored) {
      }
    }
    ServerInfo server = this.getServer(ip);
    if (server != null) {
      for (UUID uuid : uuids) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
        if (player != null) {
          if (!player.getServer().getInfo().equals(server)) {
            player.connect(server);
          }
        }
      }
      return true;
    }
    return false;
  }

  @Receptor(method = "server-name")
  public String serverName(@ParamName(name = "ip") String ip) {
    for (GuidoServer server : Guido.getConfiguration().getServers()) {
      if (server.getAddress().equalsIgnoreCase(ip)) {
        ServerInfo info = ProxyServer.getInstance().getServerInfo(server.getName());
        if (info != null) {
          return info.getName();
        }
        return null;
      }
    }
    return null;
  }

  @Nullable
  private ServerInfo getServer(@NotNull String ip) {
    for (GuidoServer server : Guido.getConfiguration().getServers()) {
      if (server.getAddress().equalsIgnoreCase(ip)) {
        return ProxyServer.getInstance().getServerInfo(server.getName());
      }
    }
    return null;
  }

  @EventHandler
  public void onPlayerDisconnect(PlayerDisconnectEvent event) {
    UUID uniqueId = event.getPlayer().getUniqueId();
    if (this.inQueue.contains(uniqueId)) {
      Guido.getClient()
          .request(
              new Request<>(
                  Boolean.class,
                  "left-queue",
                  Maps.objects("type", LinkedDataType.MINECRAFT)
                      .append("identification", Maps.singleton("uuid", UUIDUtils.trim(uniqueId)))
                      .build()),
              removed -> {
                // IGNORED
              });
    }
  }

  @Override
  public void onUnload() {}

  @Override
  public @NotNull String getName() {
    return "receptors";
  }
}
