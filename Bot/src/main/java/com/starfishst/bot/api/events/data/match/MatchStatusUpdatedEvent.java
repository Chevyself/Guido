package com.starfishst.bot.api.events.data.match;

import com.starfishst.bot.api.data.BotMatch;
import com.starfishst.guido.api.data.matches.MatchStatus;
import org.jetbrains.annotations.NotNull;

/** Called when the status of a match gets updated */
public class MatchStatusUpdatedEvent extends MatchEvent {

  /** The new status of the match */
  @NotNull private final MatchStatus status;

  /**
   * Create the match event
   *
   * @param match the match involved
   * @param status the new status of the match
   */
  public MatchStatusUpdatedEvent(@NotNull BotMatch match, @NotNull MatchStatus status) {
    super(match);
    this.status = status;
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
