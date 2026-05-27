package me.googas.bungee.commands.providers;

import com.github.chevyself.starbox.bungee.context.CommandContext;
import com.github.chevyself.starbox.bungee.providers.type.BungeeArgumentProvider;
import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import me.googas.api.API;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableType;
import me.googas.api.utility.Maps;
import me.googas.bungee.data.ProxiedOfflinePlayer;
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
      return new ProxiedOfflinePlayer(context.fromString(s, ProxiedPlayer.class, context));
    } catch (ArgumentProviderException e) {
      Linkable linkable =
          API.getLoader()
              .getLinks()
              .getLinkByRecognition(LinkableType.MINECRAFT, Maps.singleton("nickname", s));
      if (linkable != null) {
        return new ProxiedOfflinePlayer(linkable.requireMinecraftRef());
      }
    }
    throw new ArgumentProviderException(context.getMessagesProvider().invalidPlayer(s, context));
  }
}
