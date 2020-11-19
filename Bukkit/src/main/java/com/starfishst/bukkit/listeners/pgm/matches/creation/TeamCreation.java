package com.starfishst.bukkit.listeners.pgm.matches.creation;

import com.starfishst.bukkit.listeners.pgm.matches.HostedMatch;
import com.starfishst.bukkit.listeners.pgm.matches.PGMMatchMakingListener;
import org.jetbrains.annotations.NotNull;
import tc.oc.pgm.api.match.Match;

/** Helps {@link PGMMatchMakingListener} creating teams */
public interface TeamCreation {

  /**
   * Create the teams
   *
   * @param listener the listener that is hosting the match
   * @param hostedMatch the match that is being hosted
   * @param match the match waiting for the teams
   */
  void createTeams(
      @NotNull PGMMatchMakingListener listener,
      @NotNull HostedMatch hostedMatch,
      @NotNull Match match);

  /** Clears the team creator */
  void clear();
}
