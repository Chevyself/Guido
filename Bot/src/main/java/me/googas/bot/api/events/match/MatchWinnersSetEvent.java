package me.googas.bot.api.events.match;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchTeam;

/** Called when the winners of a match get set */
public class MatchWinnersSetEvent extends MatchEvent {

  /** The new winners of the match */
  @Getter private final MatchTeam winners;

  /**
   * Create the match event
   *
   * @param match the match involved
   * @param winners the winners of the match
   */
  public MatchWinnersSetEvent(@NonNull Match match, MatchTeam winners) {
    super(match);
    this.winners = winners;
  }
}
