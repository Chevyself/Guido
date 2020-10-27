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

  /**
   * Check whether the player with the given id is online the server
   *
   * @param uuid the uuid of the player to check
   * @return true if the player is inside the server
   */
  @Receptor("is-online")
  public boolean isOnline(@ParamName("uuid") UUID uuid) {
    for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
      if (player.getUniqueId().equals(uuid)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Adds to the queue the player with the given uuid
   *
   * @param uuid the uuid of the player to add to the queue
   * @return true if the player was added in the queue
   */
  @Receptor("add-queue")
  public boolean addQueue(@ParamName("uuid") UUID uuid) {
    return this.inQueue.add(uuid);
  }

  /**
   * Removes from the queue the player with the given uuid
   *
   * @param uuid the uuid of the player to add to the queue
   * @return true if the player was added in the queue
   */
  @Receptor("remove-queue")
  public boolean removeQueue(@ParamName("uuid") UUID uuid) {
    return this.inQueue.remove(uuid);
  }

  /**
   * Sends a player to a server
   *
   * @param uuid the uuid of the player to send
   * @param server the name of the server to send the player
   * @return true if the player was sent or was already on the server
   */
  @Receptor("send-to-server")
  public boolean sendToServer(@ParamName("uuid") UUID uuid, @ParamName("server") String server) {
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

  /**
   * @see #sendToServer(UUID, String) but this method sends to the server using the ip of it instead
   *     and it also accepts many players instead of one
   * @param uuidsStrings the uuids of the players in a list of string
   * @param ip the ip of the server to send
   * @return true if at least was player was sent to the server or was already in the server
   */
  @Receptor("send-to-server-by-ip")
  public boolean sendToServerByIp(
      @ParamName("uuids") List<String> uuidsStrings, @ParamName("server") String ip) {
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

  /**
   * Get the name of a server using its ip
   *
   * @param ip the ip of the server to get the name
   * @return the name of the server if the ip matches one else null
   */
  @Receptor("server-name")
  public String serverName(@ParamName("ip") String ip) {
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

  /**
   * Get a server from the given ip
   *
   * @param ip the ip to get the server from
   * @return the server if the ip matches one else null
   */
  @Nullable
  private ServerInfo getServer(@NotNull String ip) {
    for (GuidoServer server : Guido.getConfiguration().getServers()) {
      if (server.getAddress().equalsIgnoreCase(ip)) {
        return ProxyServer.getInstance().getServerInfo(server.getName());
      }
    }
    return null;
  }

  /**
   * This listener handles that a player left the server to remove them from the queue
   *
   * @param event the event of a player disconnecting from the server
   */
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
