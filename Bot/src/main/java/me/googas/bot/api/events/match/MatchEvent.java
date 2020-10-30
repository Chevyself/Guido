package me.googas.bot.api.events.match;

import me.googas.api.matches.Match;
import me.googas.bot.api.events.GuidoEvent;
import org.jetbrains.annotations.NotNull;

/** An event related to a match */
public class MatchEvent implements GuidoEvent {

  /** The match involved */
  @NotNull private final Match match;

  /**
   * Create the match event
   *
   * @param match the match involved
   */
  public MatchEvent(@NotNull Match match) {
    this.match = match;
  }

  /**
   * Get the match involved
   *
   * @return the match involved
   */
  @NotNull
  public Match getMatch() {
    return this.match;
  }

  @Override
  public String toString() {
    return "MatchEvent{" + "match=" + this.match + '}';
  }
}
