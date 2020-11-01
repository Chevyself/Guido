package me.googas.bot.api.events.match;

import me.googas.api.matches.Match;
import me.googas.api.matches.Team;
import me.googas.bot.api.events.GuidoCancellable;
import org.jetbrains.annotations.NotNull;

/** Called before a team is removed from a match */
public class MatchPreRemoveTeamEvent extends MatchEvent implements GuidoCancellable {

  /** The team that is being removed from the match */
  @NotNull private final Team team;

  /** Whether the event is cancelled */
  private boolean cancelled;
  /**
   * Create the match event
   *
   * @param match the match involved
   * @param team the team that is being removed from the match
   */
  public MatchPreRemoveTeamEvent(@NotNull Match match, @NotNull Team team) {
    super(match);
    this.team = team;
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
