package me.googas.api.client.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import me.googas.api.ValuesMap;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Implementation for match */
public class MatchImpl implements Match {

  /** The id of the match */
  @NotNull private final String id;

  /** The id of the match where it is happening */
  private final long guildId;

  /** The teams that are playing the match */
  @NotNull private final Set<Team> teams;

  /** The winners of the match */
  @Nullable private final Team winners;

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
  public MatchImpl(
      @NotNull String id,
      long guildId,
      @NotNull Set<Team> teams,
      @Nullable Team winners,
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
  public MatchImpl() {
    this("", -1, new HashSet<>(), null, new ValuesMapImpl(), MatchStatus.FINISHED);
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
    return this.winners;
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
  public @NotNull Match refresh() {
    return this;
  }

  @Override
  public void reduceTime(long l) {}

  @Override
  public void onSecondPassed() {}

  @Override
  public void onRemove() {}

  @Override
  public long getSecondsLeft() {
    return 0;
  }

  @Override
  public String toString() {
    return "MatchImpl{"
        + "id='"
        + this.id
        + '\''
        + ", guildId="
        + this.guildId
        + ", teams="
        + this.teams
        + ", winners="
        + this.winners
        + ", details="
        + this.details
        + ", status="
        + this.status
        + '}';
  }
}
