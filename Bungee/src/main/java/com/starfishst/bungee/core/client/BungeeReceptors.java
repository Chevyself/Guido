package com.starfishst.bungee.core.client;

import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.api.configuration.GuidoServer;
import com.starfishst.bungee.api.events.GuidoListener;
import com.starfishst.bungee.core.lang.BungeeLocaleFile;
import com.starfishst.bungee.utils.BungeeUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import me.googas.api.client.data.LinkableInfoImpl;
import me.googas.api.client.data.ValuesMapImpl;
import me.googas.api.links.LinkableDataType;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;
import me.googas.messaging.json.client.JsonClient;
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
e   */
  @Receptor("add-queue")
  public boolean addQueue(@ParamName("uuid") UUID uuid) {
    Guido.getLogger().info("Adding to queue " + uuid);
    return this.inQueue.add(uuid);
  }

  /**
   * Send a message to a player
   *
   * @param uuid the uuid of the player to send the message
   * @param message the message to send
   * @return true if the message was sent
   */
  @Receptor("send-message")
  public boolean sendMessage(
      @ParamName("uuid") UUID uuid, @ParamName("message") @NotNull String message) {
    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
    if (player != null) {
      player.sendMessage(BungeeUtils.getComponent(message));
      return true;
    }
    return false;
  }

  /**
   * Removes from the queue the player with the given uuid
   *
   * @param uuid the uuid of the player to add to the queue
   * @return true if the player was added in the queue
   */
  @Receptor("remove-queue")
  public boolean removeQueue(@ParamName("uuid") UUID uuid) {
    Guido.getLogger().info("Removing from queue " + uuid);
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
    Guido.getLogger().info("sending " + uuids + " to " + server);
    if (server != null) {
      for (UUID uuid : uuids) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
        if (player != null) {
          BungeeLocaleFile locale = Guido.getLanguageHandler().getFile(player);
          if (!player.getServer().getInfo().equals(server)) {
            AtomicBoolean connected = new AtomicBoolean(false);
            AtomicInteger taskId = new AtomicInteger(-1);
            taskId.set(
                ProxyServer.getInstance()
                    .getScheduler()
                    .schedule(
                        Guido.validated(),
                        () -> {
                          if (!connected.get()) {
                            player.sendMessage(
                                locale.getComponent(
                                    "receptors.being-connected",
                                    Maps.singleton("server", server.getName())));
                            player.connect(
                                server,
                                (result, error) -> {
                                  if (error != null) {
                                    connected.set(false);
                                    player.sendMessage(
                                        locale.getComponent(
                                            "receptors.connected-error",
                                            Maps.singleton("server", server.getName())));
                                  } else {
                                    player.sendMessage(
                                        locale.getComponent(
                                            "receptors.connected",
                                            Maps.singleton("server", server.getName())));
                                    connected.set(result);
                                  }
                                });
                          } else {
                            ProxyServer.getInstance().getScheduler().cancel(taskId.get());
                          }
                        },
                        0,
                        5,
                        TimeUnit.SECONDS)
                    .getId());
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
      Guido.getLogger().info(ip);
      if (server.getAddress().equalsIgnoreCase(ip)) {
        return ProxyServer.getInstance().getServerInfo(server.getName());
      }
      if (server.getAddress().startsWith("localhost")) {
        if (server.getAddress().substring(9).equalsIgnoreCase(ip)) {
          return ProxyServer.getInstance().getServerInfo(server.getName());
        }
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
      JsonClient connection = Guido.getClient().getConnection();
      if (connection != null) {
        connection.sendRequest(
            new Request<>(
                Boolean.class,
                "left-queue",
                Maps.singleton(
                    "info",
                    new LinkableInfoImpl(
                        LinkableDataType.MINECRAFT,
                        new ValuesMapImpl(Maps.singleton("uuid", uniqueId))))),
            bol -> {
              Guido.getLogger().info(uniqueId + " left the queue");
            });
      }
    }
  }

  @Override
  public void onUnload() {}

  @Override
  public @NotNull String getName() {
    return "receptors";
  }
}
