package com.starfishst.bungee.core.commands.providers;

import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.context.CommandContext;
import com.starfishst.bungee.core.data.ProxiedOfflinePlayer;
import com.starfishst.bungee.providers.type.BungeeArgumentProvider;
import com.starfishst.core.exceptions.ArgumentProviderException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableType;
import me.googas.messaging.api.MessengerListenFailException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/** Provides offline player to commands */
public class ProxiedOfflinePlayerProvider implements BungeeArgumentProvider<ProxiedOfflinePlayer> {

  @Override
  public @NonNull List<String> getSuggestions(CommandContext commandContext) {
    List<String> names = new ArrayList<>();
    for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
      names.add(player.getName());
    }
    return names;
  }

  @Override
  public @NonNull Class<ProxiedOfflinePlayer> getClazz() {
    return ProxiedOfflinePlayer.class;
  }

  @NonNull
  @Override
  public ProxiedOfflinePlayer fromString(@NonNull String s, @NonNull CommandContext context)
      throws ArgumentProviderException {
    try {
      return new ProxiedOfflinePlayer(context.get(s, ProxiedPlayer.class, context));
    } catch (ArgumentProviderException e) {
      try {
        Linkable linkable =
            Requests.Links.getLinkByRecognition(
                    LinkableType.MINECRAFT, new SimpleValuesMap("nickname", s))
                .send(Guido.getClient().validatedConnection());
        if (linkable != null) {
          return new ProxiedOfflinePlayer(linkable.requireMinecraftRef());
        }
      } catch (MessengerListenFailException | IOException ex) {
        throw new ArgumentProviderException("&c" + ex.getMessage());
      }
    }
    throw new ArgumentProviderException(context.getMessagesProvider().invalidPlayer(s, context));
  }
}
