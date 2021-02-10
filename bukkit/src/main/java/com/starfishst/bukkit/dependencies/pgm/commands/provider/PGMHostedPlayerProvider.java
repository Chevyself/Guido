package com.starfishst.bukkit.dependencies.pgm.commands.provider;

import com.starfishst.bukkit.Guido;
import com.starfishst.bukkit.dependencies.pgm.PGMHostedMatch;
import com.starfishst.bukkit.dependencies.pgm.PGMHostedPlayer;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.PGMMatchMakingHandler;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.creation.PickTeamSelection;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.creation.TeamCreation;
import com.starfishst.bukkit.lang.BukkitLocaleFile;
import com.starfishst.bukkit.matches.HostedPlayer;
import com.starfishst.commands.bukkit.context.CommandContext;
import com.starfishst.commands.bukkit.providers.type.BukkitArgumentProvider;
import com.starfishst.core.exceptions.ArgumentProviderException;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import me.googas.commons.maps.Maps;

public class PGMHostedPlayerProvider implements BukkitArgumentProvider<PGMHostedPlayer> {
  @Override
  public @NonNull Class<PGMHostedPlayer> getClazz() {
    return PGMHostedPlayer.class;
  }

  @Override
  public @NonNull PGMHostedPlayer fromString(@NonNull String s, @NonNull CommandContext context)
      throws ArgumentProviderException {
    BukkitLocaleFile locale = Guido.getLanguageHandler().getFile(context);
    PGMMatchMakingHandler listener = Guido.getModuleRegistry().require(PGMMatchMakingHandler.class);
    TeamCreation creation = listener.getCreation("pick");
    PGMHostedMatch match = listener.getMatch(context.getSender());
    if (match == null) throw new IllegalArgumentException(locale.get("participant-only"));
    if (creation instanceof PickTeamSelection) {
      for (HostedPlayer hosted : ((PickTeamSelection) creation).getPlayersLeft(match.getId())) {
        String nickname = hosted.getNickname();
        if (s.equalsIgnoreCase(nickname)) {
          return new PGMHostedPlayer(hosted);
        }
      }
    }
    throw new ArgumentProviderException(locale.get("invalid.player", Maps.singleton("string", s)));
  }

  @Override
  public @NonNull List<String> getSuggestions(@NonNull String s, CommandContext context) {
    List<String> names = new ArrayList<>();
    PGMMatchMakingHandler listener = Guido.getModuleRegistry().require(PGMMatchMakingHandler.class);
    TeamCreation creation = listener.getCreation("pick");
    PGMHostedMatch match = listener.getMatch(context.getSender());
    if (creation instanceof PickTeamSelection && match != null) {
      return ((PickTeamSelection) creation).getParticipantsNames(match.getId());
    }
    return names;
  }
}
