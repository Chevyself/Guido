package me.googas.bot.api.events.match;

import lombok.NonNull;
import me.googas.api.matches.Match;
import me.googas.api.matches.Team;

/** Called when the winners of a match get set */
public class MatchWinnersSetEvent extends MatchEvent {

  /** The new winners of the match */
  private final Team winners;

  /**
   * Create the match event
   *
   * @param match the match involved
   * @param winners the winners of the match
   */
  public MatchWinnersSetEvent(@NonNull Match match, Team winners) {
    super(match);
    this.winners = winners;
  }

  /**
   * Get the winners of the match
   *
   * @return the winners of the match
   */
  public Team getWinners() {
    return this.winners;
  }

  @Override
  public String toString() {
    return "MatchWinnersSetEvent{" + "winners=" + this.winners + "} " + super.toString();
  }
}
