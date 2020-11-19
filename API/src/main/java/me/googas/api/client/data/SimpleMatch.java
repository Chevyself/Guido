package me.googas.api.client.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import me.googas.api.discord.GuildData;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.Team;
import me.googas.api.utility.ValuesMap;
import me.googas.commons.builder.ToStringBuilder;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Implementation for match */
public class SimpleMatch implements Match {

  /** The id of the match */
  @NotNull private final String id;

  /** The id of the match where it is happening */
  private final long guildId;

  /** The teams that are playing the match */
  @NotNull private final Set<Team> teams;

  /** The id of the team winners of the match */
  private final int winners;

  /** The details of the match */
  @NotNull private final ValuesMap details;

  /** The status of the match */
  @NotNull private final MatchStatus status;

  /**
   * Create the match
   *
   * @param id the id of the match
   * @param guildId the id of the guild where the match is being played
   * @param teams the teams playing the match
   * @param winners the winners of the match
   * @param details the details of the match
   * @param status the status of the match
   */
  public SimpleMatch(
      @NotNull String id,
      long guildId,
      @NotNull Set<Team> teams,
      int winners,
      @NotNull ValuesMap details,
      @NotNull MatchStatus status) {
    this.id = id;
    this.guildId = guildId;
    this.teams = teams;
    this.winners = winners;
    this.details = details;
    this.status = status;
  }

  /** @deprecated this may only be used by gson */
  public SimpleMatch() {
    this("", -1, new HashSet<>(), -1, new SimpleValuesMap(), MatchStatus.FINISHED);
  }

  @Override
  public @NotNull String getId() {
    return this.id;
  }

  @Override
  public long getGuildId() {
    return this.guildId;
  }

  @Override
  public @NotNull Collection<Team> getTeams() {
    return Collections.unmodifiableSet(this.teams);
  }

  @Override
  public @Nullable Team getWinners() {
    return this.getTeam(this.id);
  }

  @Override
  public @NotNull ValuesMap getDetails() {
    return this.details;
  }

  @Override
  public void setWinners(@Nullable Team winners) {}

  @Override
  public void setStatus(@NotNull MatchStatus status) {}

  @NotNull
  @Override
  public MatchStatus getStatus() {
    return this.status;
  }

  @Override
  public @Nullable GuildData getGuildData() {
    return null;
  }

  @Override
  public boolean addTeam(@NotNull Team team) {
    return false;
  }

  @Override
  public boolean removeTeam(@NotNull Team team) {
    return false;
  }

  @Override
  public void onRemove() {}

  @Override
  public @NotNull Time getToRemove() {
    return new Time(0, Unit.SECONDS);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", this.id)
        .append("guildId", this.guildId)
        .append("teams", this.teams)
        .append("winners", this.winners)
        .append("details", this.details)
        .append("status", this.status)
        .build();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || this.getClass() != object.getClass()) return false;
    SimpleMatch that = (SimpleMatch) object;
    return this.guildId == that.guildId && this.id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.guildId);
  }
}
