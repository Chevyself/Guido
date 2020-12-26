package me.googas.bot.api.events.match;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchTeam;
import me.googas.bot.api.events.GuidoCancellable;

/** Called when a matchTeam is going to be added to a match */
public class MatchPreAddTeamEvent extends MatchEvent implements GuidoCancellable {

  /** The matchTeam that is being added to the match */
  @NonNull @Getter private final MatchTeam matchTeam;
  /** Whether the event has been cancelled */
  private boolean cancelled;

  /**
   * Create the match event
   *
   * @param match the match involved
   * @param matchTeam the matchTeam that is being added to the match
   */
  public MatchPreAddTeamEvent(@NonNull Match match, @NonNull MatchTeam matchTeam) {
    super(match);
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
