package com.starfishst.bukkit.listeners.pgm.matches.creation;

import com.starfishst.bukkit.listeners.pgm.matches.PGMMatchMakingListener;
import org.jetbrains.annotations.NotNull;
import tc.oc.pgm.api.match.Match;

/** Helps {@link PGMMatchMakingListener} creating teams */
public interface TeamCreation {

  /**
   * Create the teams
   *
   * @param matchMaking the match maker to create the teams to
   * @param match the match waiting for the teams
   */
  void createTeams(@NotNull PGMMatchMakingListener matchMaking, @NotNull Match match);

  /** Clears the team creator */
  void clear();
}
