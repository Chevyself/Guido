package dev.xevy.guido.bukkit.pgm.listeners.matches.creation;

import dev.xevy.bukkit.GuidoBukkitRuntime;
import dev.xevy.bukkit.HostedPlayer;
import dev.xevy.guido.bukkit.pgm.PGMHostedMatch;
import dev.xevy.guido.bukkit.pgm.listeners.matches.PGMMatchMakingHandler;
import lombok.NonNull;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.teams.Team;

/** Helps {@link PGMMatchMakingHandler} creating teams */
public interface TeamCreation {

  @NonNull
  GuidoBukkitRuntime getRuntime();

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
    MatchPlayer player = match.getPlayer(hosted.getId());
    if (player != null) {
      this.getRuntime()
          .getModuleRegistry()
          .require(PGMMatchMakingHandler.class)
          .add(match, party, player);
    }
  }

  /** Clears the team creator */
  void clear();
}
