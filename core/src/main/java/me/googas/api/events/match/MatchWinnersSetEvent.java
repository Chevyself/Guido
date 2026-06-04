package me.googas.api.events.match;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.AbstractMatch;
import me.googas.api.matches.MatchTeam;

/** Called when the winners of a match getId set */
public class MatchWinnersSetEvent extends MatchEvent {

  /** The new winners of the match */
  @Getter private final MatchTeam winners;

  /**
   * Create the abstractMatch event
   *
   * @param abstractMatch the abstractMatch involved
   * @param winners the winners of the abstractMatch
   */
  public MatchWinnersSetEvent(@NonNull AbstractMatch abstractMatch, MatchTeam winners) {
    super(abstractMatch);
    this.winners = winners;
  }
}
