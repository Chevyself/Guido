package com.starfishst.bukkit.dependencies.pgm.listeners.matches.creation;

import com.starfishst.bukkit.dependencies.pgm.PGMHostedMatch;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.PGMMatchMakingListener;
import lombok.NonNull;
import tc.oc.pgm.api.match.Match;

/** Helps {@link PGMMatchMakingListener} creating teams */
public interface TeamCreation {

  /**
   * Create the teams
   *
   * @param listener the listener that is hosting the match
   * @param PGMHostedMatch the match that is being hosted
   * @param match the match waiting for the teams
   */
  void createTeams(
      @NonNull PGMMatchMakingListener listener,
      @NonNull PGMHostedMatch PGMHostedMatch,
      @NonNull Match match);

  /** Clears the team creator */
  void clear();
}
