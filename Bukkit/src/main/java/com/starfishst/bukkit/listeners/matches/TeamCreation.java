package com.starfishst.bukkit.listeners.matches;

import org.jetbrains.annotations.NotNull;
import tc.oc.pgm.api.match.Match;

/** Helps {@link MatchMakingListener} creating teams */
public interface TeamCreation {

  /**
   * Create the teams
   *
   * @param matchMaking the match maker to create the teams to
   */
  void createTeams(@NotNull MatchMakingListener matchMaking, @NotNull Match match);
}
