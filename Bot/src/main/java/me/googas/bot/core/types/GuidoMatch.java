package me.googas.bot.core.types;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.Team;
import me.googas.bot.api.events.match.MatchAddTeamEvent;
import me.googas.bot.api.events.match.MatchLoadedEvent;
import me.googas.bot.api.events.match.MatchPreAddTeamEvent;
import me.googas.bot.api.events.match.MatchPreRemoveTeamEvent;
import me.googas.bot.api.events.match.MatchRemoveTeamEvent;
import me.googas.bot.api.events.match.MatchStatusUpdatedEvent;
import me.googas.bot.api.events.match.MatchUnloadedEvent;
import me.googas.bot.api.events.match.MatchWinnersSetEvent;
import me.googas.bot.api.types.BotMatch;
import me.googas.bot.core.Guido;
import me.googas.bot.core.types.maps.GuidoLinkedValuesMap;
import me.googas.bot.core.types.maps.GuidoValuesMap;
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
  /** The winners id team winners of the match */
  private int winners;

  /**
   * Create the match
   *
   * @param id the id of the match
   * @param guildId the id of the guild where the match happened
   * @param status the status of the match
   * @param teams the teams inside the match
   * @param details the details of the match
   * @param winners the winners of the match
   * @param addToCache whether to add the match to cache
   */
  public GuidoMatch(
      @NotNull String id,
      long guildId,
      @NotNull MatchStatus status,
      @NotNull Set<Team> teams,
      @NotNull GuidoLinkedValuesMap details,
      int winners,
      boolean addToCache) {
    super(Time.fromString("5m"), addToCache);
    this.id = id;
    this.guildId = guildId;
    this.status = status;
    this.teams = teams;
    this.details = details;
    this.winners = winners;
    if (addToCache) {
      new MatchLoadedEvent(this).call();
    }
  }

  /**
   * Create the match in a status of waiting
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
    this(id, guildId, MatchStatus.WAITING, teams, details, -1, true);
  }

  /**
   * Create the match in a status of waiting
   *
   * @param guildId the id of the guild where the match happened
   * @param teams the teams inside the match
   * @param details the details of the match
   */
  public GuidoMatch(long guildId, @NotNull Set<Team> teams, @NotNull GuidoLinkedValuesMap details) {
    this(
        Guido.getDataLoader().nextMatchId(),
        guildId,
        MatchStatus.WAITING,
        teams,
        details,
        -1,
        false);
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoMatch() {
    this("", -1, MatchStatus.FINISHED, new HashSet<>(), new GuidoLinkedValuesMap(), -1, false);
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
    return this.getTeam(this.winners);
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
    if (winners != null) {
      this.winners = winners.getId();
    } else {
      this.winners = -1;
    }
  }

  /**
   * Set the status of the match
   *
   * @param status the new status of the match
   */
  @Override
  public void setStatus(@NotNull MatchStatus status) {
    if (!new MatchStatusUpdatedEvent(this, status).callAndGet()) {
      this.status = status;
    }
  }

  @Override
  public @NotNull GuidoMatch refresh() {
    return (GuidoMatch) super.refresh();
  }

  @Override
  public boolean addTeam(@NotNull Team team) {
    if (!this.getTeams().contains(team) && !new MatchPreAddTeamEvent(this, team).callAndGet()) {
      new MatchAddTeamEvent(this, team).call();
      this.getTeams().add(team);
      return true;
    }
    return false;
  }

  @Override
  public boolean removeTeam(@NotNull Team team) {
    if (this.getTeams().contains(team) && !new MatchPreRemoveTeamEvent(this, team).callAndGet()) {
      new MatchRemoveTeamEvent(this, team).call();
      this.getTeams().remove(team);
      return true;
    }
    return false;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GuidoMatch)) return false;

    GuidoMatch that = (GuidoMatch) o;

    if (this.guildId != that.guildId) return false;
    return this.id.equals(that.id);
  }

  @Override
  public int hashCode() {
    int result = this.id.hashCode();
    result = 31 * result + (int) (this.guildId ^ (this.guildId >>> 32));
    return result;
  }
}
