package com.starfishst.bukkit.commands.providers;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.context.CommandContext;
import com.starfishst.bukkit.listeners.matches.MatchMakingListener;
import com.starfishst.bukkit.listeners.matches.creation.PickTeamSelection;
import com.starfishst.bukkit.listeners.matches.creation.TeamCreation;
import com.starfishst.bukkit.providers.type.BukkitArgumentProvider;
import com.starfishst.core.exceptions.ArgumentProviderException;
import java.util.ArrayList;
import java.util.List;
import me.googas.api.links.LinkedInfo;
import me.googas.commons.maps.Maps;
import org.jetbrains.annotations.NotNull;

/**
 * @deprecated This provider is temporal and may change in the future as it only should be used in
 *     {@link com.starfishst.bukkit.commands.PickCommands}
 */
public class PlayerInfoProvider implements BukkitArgumentProvider<LinkedInfo> {

  @Override
  public @NotNull Class<LinkedInfo> getClazz() {
    return LinkedInfo.class;
  }

  @NotNull
  @Override
  public LinkedInfo fromString(@NotNull String s, @NotNull CommandContext context)
      throws ArgumentProviderException {
    MatchMakingListener listener = Guido.getListener(MatchMakingListener.class);
    if (listener != null) {
      TeamCreation creation = listener.getCreation("pick");
      if (creation instanceof PickTeamSelection) {
        for (LinkedInfo info : ((PickTeamSelection) creation).getPlayersLeft()) {
          String nickname = info.getIdentification().getValue("nickname", String.class);
          if (s.equalsIgnoreCase(nickname)) {
            return info;
          }
        }
      }
    }
    throw new ArgumentProviderException(
        Guido.getLanguageHandler().getFile(context).get("invalid.player", Maps.singleton("s", s)));
  }

  @Override
  public @NotNull List<String> getSuggestions(@NotNull String s, CommandContext commandContext) {
    List<String> names = new ArrayList<>();
    MatchMakingListener listener = Guido.getListener(MatchMakingListener.class);
    if (listener != null) {
      TeamCreation creation = listener.getCreation("pick");
      if (creation instanceof PickTeamSelection) {
        for (LinkedInfo info : ((PickTeamSelection) creation).getPlayersLeft()) {
          String nickname = info.getIdentification().getValue("nickname", String.class);
          if (nickname != null) {
            names.add(nickname);
          }
        }
      }
    }

    return names;
  }
}
