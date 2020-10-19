package com.starfishst.bot.api.events.data.match;

import com.starfishst.bot.api.data.BotMatch;
import org.jetbrains.annotations.NotNull;

/** Called when a match gets unloaded */
public class MatchUnloadedEvent extends MatchEvent {

  /**
   * Create the match event
   *
   * @param match the match involved
   */
  public MatchUnloadedEvent(@NotNull BotMatch match) {
    super(match);
  }
}
