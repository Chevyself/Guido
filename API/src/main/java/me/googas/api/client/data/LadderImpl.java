package me.googas.api.client.data;

import me.googas.api.discord.GuildData;
import me.googas.api.matches.Ladder;
import me.googas.api.matches.Queue;
import me.googas.api.utility.ValuesMap;
import org.jetbrains.annotations.NotNull;

/** Create a ladder */
public class LadderImpl implements Ladder {

  /** The name of the ladder */
  @NotNull private final String name;

  /** The options of the ladder */
  @NotNull private final ValuesMap options;

  /** Players per team */
  private final int playersPerTeam;

  /** The base value of the elo */
  private final int baseValue;

  /**
   * Create the ladder
   *
   * @param name the name of the ladder
   * @param options the options of the ladder
   * @param playersPerTeam players per team
   * @param baseValue the base value of the elo
   */
  public LadderImpl(
      @NotNull String name, @NotNull ValuesMap options, int playersPerTeam, int baseValue) {
    this.name = name;
    this.options = options;
    this.playersPerTeam = playersPerTeam;
    this.baseValue = baseValue;
  }

  /** @deprecated this may only be used by gson */
  public LadderImpl() {
    this("", new ValuesMapImpl(), 0, 0);
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
    return "LadderImpl{"
        + "name='"
        + this.name
        + '\''
        + ", options="
        + this.options
        + ", playersPerTeam="
        + this.playersPerTeam
        + ", baseValue="
        + this.baseValue
        + '}';
  }
}
