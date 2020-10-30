package me.googas.bot.api.events.match;

import me.googas.api.matches.Match;
import org.jetbrains.annotations.NotNull;

/** Called when a match gets loaded */
public class MatchLoadedEvent extends MatchEvent {

  /**
   * Create the match event
   *
   * @param match the match involved
   */
  public MatchLoadedEvent(@NotNull Match match) {
    super(match);
  }
}
