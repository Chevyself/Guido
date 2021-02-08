package me.googas.api.events.match;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.AbstractMatch;
import me.googas.api.matches.MatchTeam;

/** Called when a matchTeam is removed from a match */
public class MatchRemoveTeamEvent extends MatchEvent {

  /** The matchTeam that has been removed from the match */
  @NonNull @Getter private final MatchTeam matchTeam;

  /**
   * Create the abstractMatch event
   *
   * @param abstractMatch the abstractMatch involved
   * @param matchTeam the matchTeam that has been removed from the abstractMatch
   */
  public MatchRemoveTeamEvent(@NonNull AbstractMatch abstractMatch, @NonNull MatchTeam matchTeam) {
    super(abstractMatch);
    this.matchTeam = matchTeam;
  }
}
