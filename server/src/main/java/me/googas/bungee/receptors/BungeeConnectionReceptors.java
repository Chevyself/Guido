package me.googas.bungee.receptors;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.api.Requests;
import me.googas.api.utility.Maps;
import me.googas.bungee.GuidoBungee;
import me.googas.bungee.lang.BungeeLocaleFile;
import me.googas.bungee.utility.Proxy;
import me.googas.net.sockets.json.ParamName;
import me.googas.net.sockets.json.Receptor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeConnectionReceptors {

  @Receptor(Requests.Bungee.SEND_SERVER)
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

  @Receptor(Requests.Bungee.SEND_SERVER_IP)
  public boolean sendToServerByIp(
      @ParamName("uuids") List<String> strings, @ParamName("server") String ip) {
    ServerInfo server = Proxy.getServer(ip);
    if (server == null || strings.isEmpty()) return false;
    for (String string : strings) {
      try {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(UUID.fromString(string));
        if (player != null) {
          this.connect(server, player);
        }
      } catch (IllegalArgumentException ignored) {

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
            .schedule(GuidoBungee.validated(), task, 0, 5, TimeUnit.SECONDS)
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
      this.locale = GuidoBungee.getLanguageHandler().getFile(player);
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
