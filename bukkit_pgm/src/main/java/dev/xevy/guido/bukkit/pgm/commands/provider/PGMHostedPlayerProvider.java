package dev.xevy.guido.bukkit.pgm.commands.provider;

import com.github.chevyself.starbox.bukkit.context.CommandContext;
import com.github.chevyself.starbox.bukkit.providers.type.BukkitArgumentProvider;
import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import dev.xevy.bukkit.BukkitLocaleFile;
import dev.xevy.bukkit.GuidoBukkitRuntime;
import dev.xevy.bukkit.HostedPlayer;
import dev.xevy.guido.bukkit.pgm.PGMHostedMatch;
import dev.xevy.guido.bukkit.pgm.PGMHostedPlayer;
import dev.xevy.guido.bukkit.pgm.listeners.matches.PGMMatchMakingHandler;
import dev.xevy.guido.bukkit.pgm.listeners.matches.creation.PickTeamSelection;
import dev.xevy.guido.bukkit.pgm.listeners.matches.creation.TeamCreation;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import me.googas.api.matches.MinecraftTeamSelectionType;
import me.googas.api.utility.Maps;

public class PGMHostedPlayerProvider implements BukkitArgumentProvider<PGMHostedPlayer> {
  @NonNull private final GuidoBukkitRuntime runtime;

  public PGMHostedPlayerProvider(@NonNull GuidoBukkitRuntime runtime) {
    this.runtime = runtime;
  }

  @Override
  public @NonNull Class<PGMHostedPlayer> getClazz() {
    return PGMHostedPlayer.class;
  }

  @Override
  public @NonNull PGMHostedPlayer fromString(@NonNull String s, @NonNull CommandContext context)
      throws ArgumentProviderException {
    BukkitLocaleFile locale = runtime.getLanguageHandler().getFile(context);
    PGMMatchMakingHandler listener =
        runtime.getModuleRegistry().require(PGMMatchMakingHandler.class);
    TeamCreation creation = listener.getCreation(MinecraftTeamSelectionType.PICK).orElse(null);
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
    PGMMatchMakingHandler listener =
        runtime.getModuleRegistry().require(PGMMatchMakingHandler.class);
    TeamCreation creation = listener.getCreation(MinecraftTeamSelectionType.PICK).orElse(null);
    PGMHostedMatch match = listener.getMatch(context.getSender());
    if (creation instanceof PickTeamSelection && match != null) {
      return ((PickTeamSelection) creation).getParticipantsNames(match.getId());
    }
    return names;
  }
}
