package dev.xevy.guido.mc;

import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.net.sockets.json.client.JsonClient;

public class MinecraftDataSynchronize {

  public void onPlayerJoin(@NonNull JsonClient client, @NonNull MinecraftPlayer player) {
    String nickname = player.getNickname();
    String ip = player.getIp();
    Requests.MinecraftLinks.updateStatus(player.getUniqueId(), nickname, ip, true).future(client);
  }

  public void onPlayerQuit(@NonNull JsonClient client, @NonNull MinecraftPlayer player) {
    Requests.MinecraftLinks.updateOnline(player.getUniqueId(), false).future(client);
  }
}
