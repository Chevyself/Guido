package me.googas.bot.api.events.match;

import lombok.NonNull;
import me.googas.api.matches.Match;
import me.googas.api.matches.Team;
import me.googas.bot.api.events.GuidoCancellable;

/** Called when a team is going to be added to a match */
public class MatchPreAddTeamEvent extends MatchEvent implements GuidoCancellable {

  /** The team that is being added to the match */
  @NonNull private final Team team;

  /** Whether the event has been cancelled */
  private boolean cancelled;

  /**
   * Create the match event
   *
   * @param match the match involved
   * @param team the team that is being added to the match
   */
  public MatchPreAddTeamEvent(@NonNull Match match, @NonNull Team team) {
    super(match);
    this.team = team;
  }

  /**
   * Get the team that is being added to the match
   *
   * @return the team that is being added to the match
   */
  @NonNull
  public Team getTeam() {
    return this.team;
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
