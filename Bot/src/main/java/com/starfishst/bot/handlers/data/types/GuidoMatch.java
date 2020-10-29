package com.starfishst.bot.handlers.data.types;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotMatch;
import com.starfishst.bot.api.events.match.MatchLoadedEvent;
import com.starfishst.bot.api.events.match.MatchStatusUpdatedEvent;
import com.starfishst.bot.api.events.match.MatchUnloadedEvent;
import com.starfishst.bot.api.events.match.MatchWinnersSetEvent;
import com.starfishst.bot.handlers.data.types.maps.GuidoLinkedValuesMap;
import com.starfishst.bot.handlers.data.types.maps.GuidoValuesMap;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.Team;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import me.googas.commons.cache.Catchable;
import me.googas.commons.time.Time;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** An implementation for a match */
public class GuidoMatch extends Catchable implements BotMatch {

  /** The id of the match */
  @NotNull private final String id;

  /** The id of the guild where the match is happening */
  private final long guildId;

  /** The teams inside the match */
  @NotNull private final Set<Team> teams;
  /** The details of the match */
  @NotNull private final GuidoLinkedValuesMap details;
  /** The status of the match */
  @NotNull private MatchStatus status;
  /** The winners of the match */
  @Nullable private Team winners;

  /**
   * Create the match
   *
   * @param id the id of the match
   * @param guildId the id of the guild where the match happened
   * @param status the status of the match
   * @param teams the teams inside the match
   * @param details the details of the match
   * @param winners the winners of the match
   */
  public GuidoMatch(
      @NotNull String id,
      long guildId,
      @NotNull MatchStatus status,
      @NotNull Set<Team> teams,
      @NotNull GuidoLinkedValuesMap details,
      @Nullable Team winners) {
    super(Time.fromString("5m"));
    this.id = id;
    this.guildId = guildId;
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
   * @param guildId the id of the guild where the match happened
   * @param teams the teams inside the match
   * @param details the details of the match
   */
  public GuidoMatch(
      @NotNull String id,
      long guildId,
      @NotNull Set<Team> teams,
      @NotNull GuidoLinkedValuesMap details) {
    this(id, guildId, MatchStatus.WAITING, teams, details, null);
  }

  /**
   * Create the match in a status of starting
   *
   * @param guildId the id of the guild where the match happened
   * @param teams the teams inside the match
   * @param details the details of the match
   */
  public GuidoMatch(long guildId, @NotNull Set<Team> teams, @NotNull GuidoLinkedValuesMap details) {
    this(Guido.getDataLoader().nextMatchId(), guildId, MatchStatus.WAITING, teams, details, null);
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoMatch() {
    this("", -1, MatchStatus.FINISHED, new HashSet<>(), new GuidoLinkedValuesMap(), null);
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

  @Override
  public long getGuildId() {
    return this.guildId;
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
  public @NotNull Collection<Team> getTeams() {
    return this.teams;
  }

  @Override
  public @Nullable Team getWinners() {
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
    new MatchWinnersSetEvent(this, winners);
    this.winners = winners;
  }

  /**
   * Set the status of the match
   *
   * @param status the new status of the match
   */
  @Override
  public void setStatus(@NotNull MatchStatus status) {
    if (new MatchStatusUpdatedEvent(this, status).callAndGet()) {
      this.status = status;
    }
  }

  @Override
  public @NotNull GuidoMatch refresh() {
    return (GuidoMatch) super.refresh();
  }

  @Override
  public String toString() {
    return "GuidoMatch{"
        + "id='"
        + this.id
        + '\''
        + ", guildId="
        + this.guildId
        + ", teams="
        + this.teams
        + ", details="
        + this.details
        + ", status="
        + this.status
        + ", winners="
        + this.winners
        + "} "
        + super.toString();
  }
}
