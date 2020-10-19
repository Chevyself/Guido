package com.starfishst.bot.api.events.data.match;

import com.starfishst.bot.api.data.BotMatch;
import com.starfishst.bot.api.events.GuidoEvent;
import org.jetbrains.annotations.NotNull;

/** An event related to a match */
public class MatchEvent implements GuidoEvent {

  /** The match involved */
  @NotNull private final BotMatch match;

  /**
   * Create the match event
   *
   * @param match the match involved
   */
  public MatchEvent(@NotNull BotMatch match) {
    this.match = match;
  }

  /**
   * Get the match involved
   *
   * @return the match involved
   */
  @NotNull
  public BotMatch getMatch() {
    return this.match;
  }

  @Override
  public String toString() {
    return "MatchEvent{" + "match=" + this.match + '}';
  }
}
