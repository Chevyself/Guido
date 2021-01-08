package com.starfishst.bungee.core.client.receptors;

import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.utils.BungeeUtils;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeMessagingReceptors {

  @Receptor(Requests.Bungee.SEND_MESSAGE)
  public boolean sendMessage(
      @ParamName("uuid") UUID uuid, @ParamName("message") @NonNull String message) {
    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
    if (player != null) {
      player.sendMessage(BungeeUtils.getComponent(message));
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
          Guido.getLanguageHandler().getFile(player).getComponent(key, placeholders));
      return true;
    }
    return false;
  }
}
