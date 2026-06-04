package me.googas.api.events.match;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.AbstractMatch;
import me.googas.api.matches.MatchTeam;

/** Called when a matchTeam has been added to a match */
public class MatchAddTeamEvent extends MatchEvent {

  /** The matchTeam that has been added to the match */
  @NonNull @Getter private final MatchTeam matchTeam;

  /**
   * Create the abstractMatch event
   *
   * @param abstractMatch the abstractMatch involved
   * @param matchTeam the matchTeam that has been added to the abstractMatch
   */
  public MatchAddTeamEvent(@NonNull AbstractMatch abstractMatch, @NonNull MatchTeam matchTeam) {
    super(abstractMatch);
    this.matchTeam = matchTeam;
  }
}
