package com.starfishst.bungee.core.commands.providers;

import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.context.CommandContext;
import com.starfishst.bungee.core.data.ProxiedOfflinePlayer;
import com.starfishst.bungee.providers.type.BungeeArgumentProvider;
import com.starfishst.core.exceptions.ArgumentProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

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
        return new ProxiedOfflinePlayer(player.getUniqueId());
      }
    }
    UUID uuid =
        Guido.getClient()
            .request(new Request<>(UUID.class, "get-uuid", Maps.objects("nickname", s).build()));
    if (uuid != null) {
      return new ProxiedOfflinePlayer(uuid);
    }
    throw new ArgumentProviderException("&c" + s + " is not a valid player");
  }
}
