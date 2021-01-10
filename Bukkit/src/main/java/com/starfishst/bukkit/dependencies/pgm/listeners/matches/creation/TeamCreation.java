package com.starfishst.bukkit.dependencies.pgm.listeners.matches.creation;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.dependencies.pgm.PGMHostedMatch;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.PGMMatchMakingHandler;
import com.starfishst.bukkit.matches.HostedPlayer;
import lombok.NonNull;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.teams.Team;

/** Helps {@link PGMMatchMakingHandler} creating teams */
public interface TeamCreation {

  /**
   * Create the teams
   *
   * @param listener the listener that is hosting the match
   * @param PGMHostedMatch the match that is being hosted
   * @param match the match waiting for the teams
   */
  void createTeams(
      @NonNull PGMMatchMakingHandler listener,
      @NonNull PGMHostedMatch PGMHostedMatch,
      @NonNull Match match);

  default void setParty(@NonNull HostedPlayer hosted, Team party, Match match) {
    MatchPlayer player = match.getPlayer(hosted.getUniqueId());
    if (player != null) {
      Guido.getHandlerRegistry()
          .requireHandler(PGMMatchMakingHandler.class)
          .add(match, party, player);
    }
  }

  /** Clears the team creator */
  void clear();
}
