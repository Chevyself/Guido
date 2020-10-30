package me.googas.bot.handlers.data.types;

import me.googas.api.discord.GuildData;
import me.googas.api.matches.Ladder;
import me.googas.api.matches.Queue;
import me.googas.bot.handlers.data.types.maps.GuidoValuesMap;
import me.googas.bot.handlers.data.types.queues.GuidoPGMQueue;
import me.googas.bot.handlers.data.types.queues.GuidoQueue;
import org.jetbrains.annotations.NotNull;

/** An implementation for ladder */
public class GuidoLadder implements Ladder {

  /** The name of the ladder */
  @NotNull private final String name;

  /** The players per team in the ladder */
  private final int playersPerTeam;

  /** The base value of the ladder */
  private final int baseValue;

  /** The options for the ladder to make each ladder unique */
  @NotNull private final GuidoValuesMap options;

  /**
   * Create the ladder
   *
   * @param name the name of the ladder
   * @param playersPerTeam the players per team in the ladder
   * @param baseValue the base value of the ladder
   * @param options the options of the ladder
   */
  public GuidoLadder(
      @NotNull String name, int playersPerTeam, int baseValue, @NotNull GuidoValuesMap options) {
    this.name = name;
    this.playersPerTeam = playersPerTeam;
    this.baseValue = baseValue;
    this.options = options;
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoLadder() {
    this("", 5, 500, new GuidoValuesMap());
  }

  @Override
  public @NotNull Queue createQueue(@NotNull GuildData data) {
    String type = this.getOptions().getValueOr("type", String.class, "none");
    switch (type) {
      case "pgm":
        return new GuidoPGMQueue(data.getId(), this.getName());
      default:
        return new GuidoQueue(data.getId(), this.getName());
    }
  }

  /**
   * Get the name of the ladder
   *
   * @return the name of the ladder
   */
  @Override
  public @NotNull String getName() {
    return this.name;
  }

  @Override
  public @NotNull GuidoValuesMap getOptions() {
    return this.options;
  }

  /**
   * Get the number of players per team
   *
   * @return the number of players per team
   */
  @Override
  public int playersPerTeam() {
    return this.playersPerTeam;
  }

  /**
   * Get the base value which all the players start with
   *
   * @return the base value
   */
  @Override
  public int baseValue() {
    return this.baseValue;
  }
}
