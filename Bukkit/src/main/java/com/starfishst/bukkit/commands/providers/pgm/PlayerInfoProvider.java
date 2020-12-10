package com.starfishst.bukkit.commands.providers.pgm;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.context.CommandContext;
import com.starfishst.bukkit.lang.BukkitLocaleFile;
import com.starfishst.bukkit.listeners.pgm.matches.HostedMatch;
import com.starfishst.bukkit.listeners.pgm.matches.PGMMatchMakingListener;
import com.starfishst.bukkit.listeners.pgm.matches.creation.PickTeamSelection;
import com.starfishst.bukkit.listeners.pgm.matches.creation.TeamCreation;
import com.starfishst.bukkit.providers.type.BukkitArgumentProvider;
import com.starfishst.core.exceptions.ArgumentProviderException;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import me.googas.api.links.LinkableInfo;
import me.googas.commons.maps.Maps;

/**
 * @deprecated This provider is temporal and may change in the future as it only should be used in
 *     {@link com.starfishst.bukkit.commands.PickCommands}
 */
public class PlayerInfoProvider implements BukkitArgumentProvider<LinkableInfo> {

  @Override
  public @NonNull Class<LinkableInfo> getClazz() {
    return LinkableInfo.class;
  }

  @NonNull
  @Override
  public LinkableInfo fromString(@NonNull String s, @NonNull CommandContext context)
      throws ArgumentProviderException {
    BukkitLocaleFile locale = Guido.getLanguageHandler().getFile(context);
    PGMMatchMakingListener listener = Guido.getListener(PGMMatchMakingListener.class);
    if (listener != null) {
      TeamCreation creation = listener.getCreation("pick");
      HostedMatch match = listener.getMatch(context.getSender());
      if (match == null) throw new IllegalArgumentException(locale.get("participant-only"));
      if (creation instanceof PickTeamSelection) {
        for (LinkableInfo info : ((PickTeamSelection) creation).getPlayersLeft(match.getId())) {
          String nickname = info.getIdentification().get("nickname", String.class);
          if (s.equalsIgnoreCase(nickname)) {
            return info;
          }
        }
      }
    }
    throw new ArgumentProviderException(locale.get("invalid.player", Maps.singleton("string", s)));
  }

  @Override
  public @NonNull List<String> getSuggestions(@NonNull String s, @NonNull CommandContext context) {
    List<String> names = new ArrayList<>();
    PGMMatchMakingListener listener = Guido.getListener(PGMMatchMakingListener.class);
    if (listener != null) {
      TeamCreation creation = listener.getCreation("pick");
      HostedMatch match = listener.getMatch(context.getSender());
      if (creation instanceof PickTeamSelection && match != null) {
        return ((PickTeamSelection) creation).getParticipantsNames(match.getId());
      }
    }

    return names;
  }
}
