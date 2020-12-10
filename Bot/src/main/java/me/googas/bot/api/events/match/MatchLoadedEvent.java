package me.googas.bot.api.events.match;

import lombok.NonNull;
import me.googas.api.matches.Match;

/** Called when a match gets loaded */
public class MatchLoadedEvent extends MatchEvent {

  /**
   * Create the match event
   *
   * @param match the match involved
   */
  public MatchLoadedEvent(@NonNull Match match) {
    super(match);
  }
}
