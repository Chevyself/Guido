package me.googas.bungee.receptors;

import com.github.chevyself.starbox.common.Components;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.bungee.GuidoBungee;
import me.googas.net.sockets.json.ParamName;
import me.googas.net.sockets.json.Receptor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeMessagingReceptors {

  @Receptor(Requests.Bungee.SEND_MESSAGE)
  public boolean sendMessage(
      @ParamName("uuid") UUID uuid, @ParamName("message") @NonNull String message) {
    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
    if (player != null) {
      player.sendMessage(Components.getComponent(message));
      return true;
    }
    return false;
  }

  @Receptor(Requests.Bungee.SEND_LOCALIZED)
  public boolean sendMessage(
      @ParamName("uuid") UUID uuid,
      @ParamName("key") String key,
      @ParamName("placeholders") Map<String, String> placeholders) {
    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
    if (player != null) {
      player.sendMessage(
          GuidoBungee.getLanguageHandler().getFile(player).getComponent(key, placeholders));
      return true;
    }
    return false;
  }
}
