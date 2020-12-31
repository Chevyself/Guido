package me.googas.bot.api.events.data.links;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.links.Linkable;
import me.googas.api.matches.ladder.Ladder;

public class LinkableEloUpdatedEvent extends LinkableEvent {

  /** The ladder where the elo was updated */
  @Getter private final Ladder ladder;
  /** The previous elo */
  @Getter private final float previous;

  /** The new elo */
  @Getter private final float newElo;

  /** Whether the player won the match */
  @Getter private final boolean winner;

  /**
   * Create the event
   *
   * @param data the data involved in the event
   * @param ladder
   * @param previous the previous elo
   * @param newElo the new elo
   * @param winner
   */
  public LinkableEloUpdatedEvent(
      @NonNull Linkable data, Ladder ladder, float previous, float newElo, boolean winner) {
    super(data);
    this.ladder = ladder;
    this.previous = previous;
    this.newElo = newElo;
    this.winner = winner;
  }
}
