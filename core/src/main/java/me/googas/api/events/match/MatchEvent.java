package me.googas.api.events.match;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.events.GuidoEvent;
import me.googas.api.matches.AbstractMatch;

/** An event related to a abstractMatch */
public class MatchEvent implements GuidoEvent {

  /** The abstractMatch involved */
  @NonNull @Getter private final AbstractMatch abstractMatch;

  /**
   * Create the abstractMatch event
   *
   * @param abstractMatch the abstractMatch involved
   */
  public MatchEvent(@NonNull AbstractMatch abstractMatch) {
    this.abstractMatch = abstractMatch;
  }

  @Override
  public String toString() {
    return "MatchEvent{" + "abstractMatch=" + this.abstractMatch + '}';
  }
}
