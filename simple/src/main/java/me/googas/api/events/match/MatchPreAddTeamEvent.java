package me.googas.api.events.match;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.events.GuidoCancellable;
import me.googas.api.matches.AbstractMatch;
import me.googas.api.matches.MatchTeam;

/** Called when a matchTeam is going to be added to a match */
public class MatchPreAddTeamEvent extends MatchEvent implements GuidoCancellable {

  /** The matchTeam that is being added to the match */
  @NonNull @Getter private final MatchTeam matchTeam;
  /** Whether the event has been cancelled */
  private boolean cancelled;

  /**
   * Create the abstractMatch event
   *
   * @param abstractMatch the abstractMatch involved
   * @param matchTeam the matchTeam that is being added to the abstractMatch
   */
  public MatchPreAddTeamEvent(@NonNull AbstractMatch abstractMatch, @NonNull MatchTeam matchTeam) {
    super(abstractMatch);
    this.matchTeam = matchTeam;
  }

  @Override
  public void setCancelled(boolean b) {
    this.cancelled = b;
  }

  @Override
  public boolean isCancelled() {
    return this.cancelled;
  }
}
