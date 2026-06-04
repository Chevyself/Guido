package me.googas.api.events.match;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.events.GuidoCancellable;
import me.googas.api.matches.AbstractMatch;
import me.googas.api.matches.MatchStatus;

/** Called when the status of a match gets updated */
public class MatchStatusUpdatedEvent extends MatchEvent implements GuidoCancellable {

  /** The new status of the match */
  @NonNull @Getter private final MatchStatus status;

  /** Whether the event is cancelled */
  private boolean cancelled;

  /**
   * Create the abstractMatch event
   *
   * @param abstractMatch the abstractMatch involved
   * @param status the new status of the abstractMatch
   */
  public MatchStatusUpdatedEvent(
      @NonNull AbstractMatch abstractMatch, @NonNull MatchStatus status) {
    super(abstractMatch);
    this.status = status;
  }

  @Override
  public void setCancelled(boolean b) {
    this.cancelled = b;
  }

  @Override
  public boolean isCancelled() {
    return this.cancelled;
  }
}
