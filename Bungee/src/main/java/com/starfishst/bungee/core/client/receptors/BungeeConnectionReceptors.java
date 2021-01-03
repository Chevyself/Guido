package com.starfishst.bungee.core.client.receptors;

import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.core.lang.BungeeLocaleFile;
import com.starfishst.bungee.core.utility.Proxy;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.commons.maps.Maps;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeConnectionReceptors {

  @Receptor("bungee/send-to-server")
  public boolean sendToServer(@ParamName("uuid") UUID uuid, @ParamName("server") String server) {
    ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server);
    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
    if (player != null && serverInfo != null) {
      if (!player.getServer().getInfo().equals(serverInfo)) {
        this.connect(serverInfo, player);
      }
      return true;
    }
    return false;
  }

  @Receptor("bungee/send-to-server-by-ip")
  public boolean sendToServerByIp(
      @ParamName("uuids") List<UUID> uuids, @ParamName("server") String ip) {
    ServerInfo server = Proxy.getServer(ip);
    if (server == null || uuids.isEmpty()) return false;
    for (UUID uuid : uuids) {
      ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
      if (player != null) {
        this.connect(server, player);
      }
    }
    return true;
  }

  public void connect(@NonNull ServerInfo server, @NonNull ProxiedPlayer player) {
    if (player.getServer().getInfo().equals(server)) return;
    ConnectionTask task = new ConnectionTask(player, server);
    task.setTaskId(
        ProxyServer.getInstance()
            .getScheduler()
            .schedule(Guido.validated(), task, 0, 5, TimeUnit.SECONDS)
            .getId());
  }

  static class ConnectionTask implements Runnable {

    @NonNull @Getter private final AtomicBoolean connected = new AtomicBoolean(false);
    @NonNull @Getter private final ProxiedPlayer player;
    @NonNull private final BungeeLocaleFile locale;
    @NonNull @Getter private final ServerInfo server;
    @Getter @Setter private int taskId = -1;

    ConnectionTask(@NonNull ProxiedPlayer player, @NonNull ServerInfo server) {
      this.player = player;
      this.locale = Guido.getLanguageHandler().getFile(player);
      this.server = server;
    }

    @Override
    public void run() {
      if (this.connected.get()) {
        ProxyServer.getInstance().getScheduler().cancel(this.taskId);
        return;
      }
      this.player.sendMessage(
          this.locale.getComponent(
              "receptors.being-connected", Maps.singleton("server", this.server.getName())));
      this.player.connect(
          this.server,
          (result, error) -> {
            if (error != null) {
              this.player.sendMessage(
                  this.locale.getComponent(
                      "receptors.connected-error",
                      Maps.singleton("server", this.server.getName())));
              this.connected.set(false);
            } else {
              this.player.sendMessage(
                  this.locale.getComponent(
                      "receptors.connected", Maps.singleton("server", this.server.getName())));
              this.connected.set(result);
            }
          });
    }
  }
}
