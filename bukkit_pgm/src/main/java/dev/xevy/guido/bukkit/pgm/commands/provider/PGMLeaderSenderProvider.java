package dev.xevy.guido.bukkit.pgm.commands.provider;

import com.github.chevyself.starbox.bukkit.context.CommandContext;
import com.github.chevyself.starbox.bukkit.providers.type.BukkitExtraArgumentProvider;
import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import dev.xevy.bukkit.BukkitLocaleFile;
import dev.xevy.bukkit.GuidoBukkitRuntime;
import dev.xevy.guido.bukkit.pgm.PGMHostedMatch;
import dev.xevy.guido.bukkit.pgm.PGMLeader;
import dev.xevy.guido.bukkit.pgm.listeners.matches.PGMMatchMakingHandler;
import dev.xevy.guido.bukkit.pgm.listeners.matches.creation.PickTeamSelection;
import dev.xevy.guido.bukkit.pgm.listeners.matches.creation.TeamCreation;
import lombok.NonNull;
import me.googas.api.matches.MinecraftTeamSelectionType;
import org.bukkit.entity.Player;

public class PGMLeaderSenderProvider implements BukkitExtraArgumentProvider<PGMLeader> {
  @NonNull private final GuidoBukkitRuntime runtime;

  public PGMLeaderSenderProvider(@NonNull GuidoBukkitRuntime runtime) {
    this.runtime = runtime;
  }

  @Override
  public @NonNull Class<PGMLeader> getClazz() {
    return PGMLeader.class;
  }

  @Override
  public @NonNull PGMLeader getObject(@NonNull CommandContext context)
      throws ArgumentProviderException {
    PGMMatchMakingHandler listener =
        runtime.getModuleRegistry().require(PGMMatchMakingHandler.class);
    BukkitLocaleFile locale = runtime.getLanguageHandler().getFile(context);
    if (context.getSender() instanceof Player) {
      TeamCreation pick = listener.getCreation(MinecraftTeamSelectionType.PICK).orElse(null);
      PGMHostedMatch match = listener.getMatch(context.getSender());
      if (match == null) throw new IllegalArgumentException(locale.get("participant-only"));
      if (pick instanceof PickTeamSelection) {
        for (PickTeamSelection.SelectingTeam team :
            ((PickTeamSelection) pick).getTeams(match.getId())) {
          if (team.getLeaderUniqueId().equals(((Player) context.getSender()).getUniqueId())) {
            return new PGMLeader(team.getLeader());
          }
        }
      }
    }
    throw new ArgumentProviderException(locale.get("captain-only"));
  }
}
