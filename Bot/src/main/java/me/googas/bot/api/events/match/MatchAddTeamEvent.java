package me.googas.bot.api.events.match;

import lombok.NonNull;
import me.googas.api.matches.Match;
import me.googas.api.matches.Team;

/** Called when a team has been added to a match */
public class MatchAddTeamEvent extends MatchEvent {

  /** The team that has been added to the match */
  @NonNull private final Team team;

  /**
   * Create the match event
   *
   * @param match the match involved
   * @param team the team that has been added to the match
   */
  public MatchAddTeamEvent(@NonNull Match match, @NonNull Team team) {
    super(match);
    this.team = team;
  }

  /**
   * Get the team that has been added to the match
   *
   * @return the team that is has been added
   */
  @NonNull
  public Team getTeam() {
    return this.team;
  }
}
