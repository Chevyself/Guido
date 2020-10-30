package me.googas.bot.api.events.match;

import me.googas.api.matches.Match;
import org.jetbrains.annotations.NotNull;

/** Called when a match gets unloaded */
public class MatchUnloadedEvent extends MatchEvent {

  /**
   * Create the match event
   *
   * @param match the match involved
   */
  public MatchUnloadedEvent(@NotNull Match match) {
    super(match);
  }
}
