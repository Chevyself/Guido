package me.googas.bot.core.types;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.Team;
import me.googas.bot.api.events.match.MatchAddTeamEvent;
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
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;

/** An implementation for a match */
public class GuidoMatch implements BotMatch {

  /** The id of the match */
  @NonNull private final String id;

  /** The id of the guild where the match is happening */
  private final long guildId;

  /** The teams inside the match */
  @NonNull private final Set<Team> teams;
  /** The details of the match */
  @NonNull private final GuidoLinkedValuesMap details;
  /** The status of the match */
  @NonNull private MatchStatus status;
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
   */
  public GuidoMatch(
      @NonNull String id,
      long guildId,
      @NonNull MatchStatus status,
      @NonNull Set<Team> teams,
      @NonNull GuidoLinkedValuesMap details,
      int winners) {
    this.id = id;
    this.guildId = guildId;
    this.status = status;
    this.teams = teams;
    this.details = details;
    this.winners = winners;
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
      @NonNull String id,
      long guildId,
      @NonNull Set<Team> teams,
      @NonNull GuidoLinkedValuesMap details) {
    this(id, guildId, MatchStatus.WAITING, teams, details, -1);
  }

  /**
   * Create the match in a status of waiting
   *
   * @param guildId the id of the guild where the match happened
   * @param teams the teams inside the match
   * @param details the details of the match
   */
  public GuidoMatch(long guildId, @NonNull Set<Team> teams, @NonNull GuidoLinkedValuesMap details) {
    this(Guido.getDataLoader().nextMatchId(), guildId, MatchStatus.WAITING, teams, details, -1);
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoMatch() {
    this("", -1, MatchStatus.FINISHED, new HashSet<>(), new GuidoLinkedValuesMap(), -1);
  }

  @Override
  public void onRemove() {
    new MatchUnloadedEvent(this).call();
  }

  @Override
  public @NonNull Time getToRemove() {
    return new Time(3, Unit.MINUTES);
  }

  @Override
  public @NonNull String getId() {
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
  @NonNull
  @Override
  public MatchStatus getStatus() {
    return this.status;
  }

  @Override
  public @NonNull Collection<Team> getTeams() {
    return this.teams;
  }

  @Override
  public Team getWinners() {
    return this.getTeam(this.winners);
  }

  @Override
  public @NonNull GuidoValuesMap getDetails() {
    return this.details;
  }

  /**
   * Set the winners of the match
   *
   * @param winners the winners of the match
   */
  @Override
  public void setWinners(Team winners) {
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
  public void setStatus(@NonNull MatchStatus status) {
    if (new MatchStatusUpdatedEvent(this, status).callAndGet()) {
      this.status = status;
    }
  }

  @Override
  public boolean addTeam(@NonNull Team team) {
    if (!this.getTeams().contains(team) && new MatchPreAddTeamEvent(this, team).callAndGet()) {
      new MatchAddTeamEvent(this, team).call();
      this.getTeams().add(team);
      Team participants = this.getTeam(-2);
      if (participants != null) this.removeTeam(participants);
      return true;
    }
    return false;
  }

  @Override
  public boolean removeTeam(@NonNull Team team) {
    if (this.getTeams().contains(team) && new MatchPreRemoveTeamEvent(this, team).callAndGet()) {
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

  /**
   * Adds this catchable in cache
   *
   * @return this same object instance
   */
  @Override
  public @NonNull GuidoMatch cache() {
    return (GuidoMatch) BotMatch.super.cache();
  }
}
