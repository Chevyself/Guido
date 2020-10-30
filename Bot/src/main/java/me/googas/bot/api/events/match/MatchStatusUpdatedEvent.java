package me.googas.bot.api.events.match;

import me.googas.api.matches.Match;
import me.googas.api.matches.MatchStatus;
import me.googas.bot.api.events.GuidoCancellable;
import org.jetbrains.annotations.NotNull;

/** Called when the status of a match gets updated */
public class MatchStatusUpdatedEvent extends MatchEvent implements GuidoCancellable {

  /** The new status of the match */
  @NotNull private final MatchStatus status;

  /** Whether the event is cancelled */
  private boolean cancelled;

  /**
   * Create the match event
   *
   * @param match the match involved
   * @param status the new status of the match
   */
  public MatchStatusUpdatedEvent(@NotNull Match match, @NotNull MatchStatus status) {
    super(match);
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

  /**
   * Get the new status of the match
   *
   * @return the new status of the match
   */
  @NotNull
  public MatchStatus getStatus() {
    return this.status;
  }
}
