package me.googas.bot.api.events.match;

import me.googas.api.matches.Match;
import me.googas.api.matches.Team;
import me.googas.bot.api.events.GuidoEvent;
import org.jetbrains.annotations.NotNull;

/** Called when a team is removed from a match */
public class MatchRemoveTeamEvent extends MatchEvent implements GuidoEvent {

  /** The team that has been removed from the match */
  @NotNull private final Team team;

  /**
   * Create the match event
   *
   * @param match the match involved
   * @param team the team that has been removed from the match
   */
  public MatchRemoveTeamEvent(@NotNull Match match, @NotNull Team team) {
    super(match);
    this.team = team;
  }
}
