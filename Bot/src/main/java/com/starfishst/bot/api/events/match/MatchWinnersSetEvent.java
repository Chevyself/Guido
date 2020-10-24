package com.starfishst.bot.api.events.match;

import com.starfishst.bot.api.data.BotMatch;
import com.starfishst.guido.api.data.matches.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Called when the winners of a match get set */
public class MatchWinnersSetEvent extends MatchEvent {

  /** The new winners of the match */
  @Nullable private final Team winners;

  /**
   * Create the match event
   *
   * @param match the match involved
   * @param winners the winners of the match
   */
  public MatchWinnersSetEvent(@NotNull BotMatch match, @Nullable Team winners) {
    super(match);
    this.winners = winners;
  }

  /**
   * Get the winners of the match
   *
   * @return the winners of the match
   */
  @Nullable
  public Team getWinners() {
    return this.winners;
  }

  @Override
  public String toString() {
    return "MatchWinnersSetEvent{" + "winners=" + this.winners + "} " + super.toString();
  }
}
