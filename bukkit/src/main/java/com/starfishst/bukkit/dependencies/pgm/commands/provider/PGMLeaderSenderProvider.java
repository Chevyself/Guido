package com.starfishst.bukkit.dependencies.pgm.commands.provider;

import com.starfishst.bukkit.Guido;
import com.starfishst.bukkit.dependencies.pgm.PGMHostedMatch;
import com.starfishst.bukkit.dependencies.pgm.PGMLeader;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.PGMMatchMakingHandler;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.creation.PickTeamSelection;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.creation.TeamCreation;
import com.starfishst.bukkit.lang.BukkitLocaleFile;
import com.starfishst.commands.bukkit.context.CommandContext;
import com.starfishst.commands.bukkit.providers.type.BukkitExtraArgumentProvider;
import com.starfishst.core.exceptions.ArgumentProviderException;
import lombok.NonNull;
import org.bukkit.entity.Player;

public class PGMLeaderSenderProvider implements BukkitExtraArgumentProvider<PGMLeader> {
  @Override
  public @NonNull Class<PGMLeader> getClazz() {
    return PGMLeader.class;
  }

  @Override
  public @NonNull PGMLeader getObject(@NonNull CommandContext context)
      throws ArgumentProviderException {
    PGMMatchMakingHandler listener = Guido.getModuleRegistry().require(PGMMatchMakingHandler.class);
    BukkitLocaleFile locale = Guido.getLanguageHandler().getFile(context);
    if (context.getSender() instanceof Player) {
      TeamCreation pick = listener.getCreation("pick");
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
