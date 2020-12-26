package me.googas.bot.api.events.match;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchTeam;

/** Called when a matchTeam has been added to a match */
public class MatchAddTeamEvent extends MatchEvent {

  /** The matchTeam that has been added to the match */
  @NonNull @Getter private final MatchTeam matchTeam;

  /**
   * Create the match event
   *
   * @param match the match involved
   * @param matchTeam the matchTeam that has been added to the match
   */
  public MatchAddTeamEvent(@NonNull Match match, @NonNull MatchTeam matchTeam) {
    super(match);
    this.matchTeam = matchTeam;
  }
}
