package com.starfishst.bot.handlers.data;

import com.starfishst.bot.api.data.BotMatch;
import com.starfishst.bot.api.events.data.match.MatchLoadedEvent;
import com.starfishst.bot.api.events.data.match.MatchUnloadedEvent;
import com.starfishst.bot.api.events.data.match.MatchWinnersSetEvent;
import com.starfishst.guido.api.data.matches.MatchStatus;
import com.starfishst.guido.api.data.matches.Team;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import me.googas.commons.cache.Catchable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** An implementation for a match */
public class GuidoMatch extends Catchable implements BotMatch {

  /** The id of the match */
  @NotNull private final String id;
  /** The teams inside the match */
  @NotNull private final Set<GuidoTeam> teams;
  /** The details of the match */
  @NotNull private final GuidoValuesMap details;
  /** The status of the match */
  @NotNull private MatchStatus status;
  /** The winners of the match */
  @Nullable private GuidoTeam winners;

  /**
   * Create the match
   *
   * @param id the id of the match
   * @param status the status of the match
   * @param teams the teams inside the match
   * @param details the details of the match
   * @param winners the winners of the match
   */
  public GuidoMatch(
      @NotNull String id,
      @NotNull MatchStatus status,
      @NotNull Set<GuidoTeam> teams,
      @NotNull GuidoValuesMap details,
      @Nullable GuidoTeam winners) {
    this.id = id;
    this.status = status;
    this.teams = teams;
    this.details = details;
    this.winners = winners;
    new MatchLoadedEvent(this).call();
  }

  /**
   * Create the match in a status of starting
   *
   * @param id the id of the match
   * @param teams the teams inside the match
   * @param details the details of the match
   */
  public GuidoMatch(
      @NotNull String id, @NotNull Set<GuidoTeam> teams, @NotNull GuidoValuesMap details) {
    this(id, MatchStatus.STARTING, teams, details, null);
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoMatch() {
    this("", MatchStatus.FINISHED, new HashSet<>(), new GuidoValuesMap(), null);
  }

  @Override
  public void onSecondPassed() {}

  @Override
  public void onRemove() {
    new MatchUnloadedEvent(this).call();
  }

  @Override
  public @NotNull String getId() {
    return this.id;
  }

  /**
   * Get the status of the match
   *
   * @return the status of the match
   */
  @NotNull
  @Override
  public MatchStatus getStatus() {
    return this.status;
  }

  @Override
  public @NotNull Collection<GuidoTeam> getTeams() {
    return this.teams;
  }

  @Override
  public @Nullable GuidoTeam getWinners() {
    return this.winners;
  }

  @Override
  public @NotNull GuidoValuesMap getDetails() {
    return this.details;
  }

  /**
   * Set the winners of the match
   *
   * @param winners the winners of the match
   */
  @Override
  public void setWinners(@Nullable Team winners) {
    if (winners instanceof GuidoTeam) {
      new MatchWinnersSetEvent(this, (GuidoTeam) winners);
      this.winners = (GuidoTeam) winners;
    } else {
      throw new IllegalArgumentException(winners + " must be a guido team");
    }
  }

  /**
   * Set the status of the match
   *
   * @param status the new status of the match
   */
  @Override
  public void setStatus(@NotNull MatchStatus status) {}
}
