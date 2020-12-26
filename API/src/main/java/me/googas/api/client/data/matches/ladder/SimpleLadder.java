package me.googas.api.client.data.matches.ladder;

import java.util.Objects;
import lombok.NonNull;
import me.googas.api.ValuesMap;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.queue.Queue;
import me.googas.commons.builder.ToStringBuilder;

public class SimpleLadder implements Ladder {

  @NonNull private final String name;
  @NonNull private final ValuesMap options;
  private final int playersPerTeam;
  private final int baseValue;
  private final int teamsPerMatch;

  /**
   * Create the ladder
   *
   * @param name the name of the ladder
   * @param options the options of the ladder
   * @param playersPerTeam players per team
   * @param baseValue the base value of the elo
   * @param teamsPerMatch the teams per match
   */
  public SimpleLadder(
      @NonNull String name,
      @NonNull ValuesMap options,
      int playersPerTeam,
      int baseValue,
      int teamsPerMatch) {
    this.name = name;
    this.options = options;
    this.playersPerTeam = playersPerTeam;
    this.baseValue = baseValue;
    this.teamsPerMatch = teamsPerMatch;
  }

  /** @deprecated this may only be used by gson */
  public SimpleLadder() {
    this("", new SimpleValuesMap(), 0, 0, 0);
  }

  @Override
  public int playersPerTeam() {
    return this.playersPerTeam;
  }

  @Override
  public int baseValue() {
    return this.baseValue;
  }

  @Override
  public int teamsPerMatch() {
    return this.teamsPerMatch;
  }

  @Override
  public @NonNull Queue createQueue(long guildId) {
    throw new UnsupportedOperationException("Implementation cannot create queues");
  }

  @Override
  public @NonNull String getName() {
    return this.name;
  }

  @Override
  public @NonNull ValuesMap getOptions() {
    return this.options;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("name", this.name)
        .append("options", this.options)
        .append("playersPerTeam", this.playersPerTeam)
        .append("baseValue", this.baseValue)
        .append("teamsPerMatch", this.teamsPerMatch)
        .build();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || this.getClass() != object.getClass()) return false;
    SimpleLadder that = (SimpleLadder) object;
    return this.name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.name);
  }
}
