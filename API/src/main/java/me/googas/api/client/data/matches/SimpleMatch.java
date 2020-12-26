package me.googas.api.client.data.matches;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.ValuesMap;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.ladder.Ladder;
import me.googas.commons.builder.ToStringBuilder;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;

public class SimpleMatch implements Match {

  @NonNull private final String id;
  private final long guildId;
  @NonNull private final Set<MatchTeam> matchTeams;
  private final int winners;
  @NonNull private final ValuesMap details;
  @NonNull private final MatchStatus status;

  /**
   * Create the match
   *
   * @param id the id of the match
   * @param guildId the id of the guild where the match is being played
   * @param matchTeams the matchTeams playing the match
   * @param winners the winners of the match
   * @param details the details of the match
   * @param status the status of the match
   */
  public SimpleMatch(
      @NonNull String id,
      long guildId,
      @NonNull Set<MatchTeam> matchTeams,
      int winners,
      @NonNull ValuesMap details,
      @NonNull MatchStatus status) {
    this.id = id;
    this.guildId = guildId;
    this.matchTeams = matchTeams;
    this.winners = winners;
    this.details = details;
    this.status = status;
  }

  /** @deprecated this may only be used by gson */
  public SimpleMatch() {
    this("", -1, new HashSet<>(), -1, new SimpleValuesMap(), MatchStatus.FINISHED);
  }

  @Override
  public Ladder getLadder() {
    return null;
  }

  @Override
  public @NonNull String getId() {
    return this.id;
  }

  @Override
  public long getGuildId() {
    return this.guildId;
  }

  @Override
  public @NonNull Collection<MatchTeam> getTeams() {
    return Collections.unmodifiableSet(this.matchTeams);
  }

  @Override
  public MatchTeam getWinners() {
    return this.getTeam(this.id);
  }

  @Override
  public @NonNull ValuesMap getDetails() {
    return this.details;
  }

  @Override
  public void setWinners(MatchTeam winners) {}

  @Override
  public void setStatus(@NonNull MatchStatus status) {}

  @NonNull
  @Override
  public MatchStatus getStatus() {
    return this.status;
  }

  @Override
  public boolean addTeam(@NonNull MatchTeam matchTeam) {
    return false;
  }

  @Override
  public boolean removeTeam(@NonNull MatchTeam matchTeam) {
    return false;
  }

  @Override
  public void onRemove() {}

  @Override
  public @NonNull Time getToRemove() {
    return new Time(0, Unit.SECONDS);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", this.id)
        .append("guildId", this.guildId)
        .append("matchTeams", this.matchTeams)
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
