package me.googas.api.client.data;

import java.util.Objects;
import me.googas.api.discord.GuildData;
import me.googas.api.matches.Ladder;
import me.googas.api.matches.Queue;
import me.googas.api.utility.ValuesMap;
import me.googas.commons.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;

/** Create a ladder */
public class SimpleLadder implements Ladder {

  /** The name of the ladder */
  @NotNull private final String name;

  /** The options of the ladder */
  @NotNull private final ValuesMap options;

  /** Players per team */
  private final int playersPerTeam;

  /** The base value of the elo */
  private final int baseValue;

  /** The teams per match */
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
      @NotNull String name,
      @NotNull ValuesMap options,
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

  /**
   * Get the number of teams per match
   *
   * @return the number of teams per match
   */
  @Override
  public int teamsPerMatch() {
    return 0;
  }

  @Override
  public @NotNull Queue createQueue(@NotNull GuildData guild) {
    throw new UnsupportedOperationException("Implementation cannot create queues");
  }

  @Override
  public @NotNull String getName() {
    return this.name;
  }

  @Override
  public @NotNull ValuesMap getOptions() {
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
