package me.googas.bot.api.events.match;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.Match;
import me.googas.bot.api.events.GuidoEvent;

/** An event related to a match */
public class MatchEvent implements GuidoEvent {

  /** The match involved */
  @NonNull @Getter private final Match match;

  /**
   * Create the match event
   *
   * @param match the match involved
   */
  public MatchEvent(@NonNull Match match) {
    this.match = match;
  }

  @Override
  public String toString() {
    return "MatchEvent{" + "match=" + this.match + '}';
  }
}
