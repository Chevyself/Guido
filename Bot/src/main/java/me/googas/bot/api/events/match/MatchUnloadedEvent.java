package me.googas.bot.api.events.match;

import lombok.NonNull;
import me.googas.api.matches.Match;

/** Called when a match gets unloaded */
public class MatchUnloadedEvent extends MatchEvent {

  /**
   * Create the match event
   *
   * @param match the match involved
   */
  public MatchUnloadedEvent(@NonNull Match match) {
    super(match);
  }
}
