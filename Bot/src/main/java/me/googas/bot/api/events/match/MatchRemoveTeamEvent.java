package me.googas.bot.api.events.match;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchTeam;
import me.googas.bot.api.events.GuidoEvent;

/** Called when a matchTeam is removed from a match */
public class MatchRemoveTeamEvent extends MatchEvent implements GuidoEvent {

  /** The matchTeam that has been removed from the match */
  @NonNull @Getter private final MatchTeam matchTeam;

  /**
   * Create the match event
   *
   * @param match the match involved
   * @param matchTeam the matchTeam that has been removed from the match
   */
  public MatchRemoveTeamEvent(@NonNull Match match, @NonNull MatchTeam matchTeam) {
    super(match);
    this.matchTeam = matchTeam;
  }
}
