package com.starfishst.bungee.core.commands.providers;

import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.context.CommandContext;
import com.starfishst.bungee.core.data.ProxiedOfflinePlayer;
import com.starfishst.bungee.providers.type.BungeeArgumentProvider;
import com.starfishst.core.exceptions.ArgumentProviderException;
import java.util.ArrayList;
import java.util.List;
import me.googas.api.links.LinkableInfo;
import me.googas.api.utility.ValuesMap;
import me.googas.commons.UUIDUtils;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import me.googas.messaging.api.MessengerListenFailException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

/** Provides offline player to commands */
public class ProxiedOfflinePlayerProvider implements BungeeArgumentProvider<ProxiedOfflinePlayer> {

  @Override
  public @NotNull List<String> getSuggestions(CommandContext commandContext) {
    List<String> names = new ArrayList<>();
    for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
      names.add(player.getName());
    }
    return names;
  }

  @Override
  public @NotNull Class<ProxiedOfflinePlayer> getClazz() {
    return ProxiedOfflinePlayer.class;
  }

  @NotNull
  @Override
  public ProxiedOfflinePlayer fromString(@NotNull String s, @NotNull CommandContext commandContext)
      throws ArgumentProviderException {
    for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
      if (player.getName().equalsIgnoreCase(s)) {
        return new ProxiedOfflinePlayer(player.getUniqueId(), player.getName());
      }
    }
    try {
      LinkableInfo playerInfo =
          Guido.getClient()
              .request(
                  new Request<>(
                      LinkableInfo.class, "get-mc-by-name", Maps.objects("nickname", s).build()));
      if (playerInfo != null) {
        ValuesMap identification = playerInfo.getIdentification();
        String uuid = identification.get("uuid", String.class);
        String nickname = identification.get("nickname", String.class);
        if (nickname != null && uuid != null) {
          return new ProxiedOfflinePlayer(UUIDUtils.untrim(uuid), nickname);
        }
      }
    } catch (MessengerListenFailException e) {
      throw new ArgumentProviderException("&cRequest timed out");
    }
    throw new ArgumentProviderException("&e" + s + "&c is not a valid player");
  }
}
