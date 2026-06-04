package me.googas.api.events.match;

import lombok.NonNull;
import me.googas.api.matches.AbstractMatch;

/** Called when a match gets unloaded */
public class MatchUnloadedEvent extends MatchEvent {

  /**
   * Create the abstractMatch event
   *
   * @param abstractMatch the abstractMatch involved
   */
  public MatchUnloadedEvent(@NonNull AbstractMatch abstractMatch) {
    super(abstractMatch);
  }
}
