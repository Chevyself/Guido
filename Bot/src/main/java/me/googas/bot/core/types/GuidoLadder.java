package me.googas.bot.core.types;

import lombok.NonNull;
import me.googas.api.discord.GuildData;
import me.googas.api.matches.Ladder;
import me.googas.api.matches.Queue;
import me.googas.bot.core.types.maps.GuidoValuesMap;
import me.googas.bot.core.types.queues.GuidoPGMQueue;
import me.googas.bot.core.types.queues.GuidoQueue;

/** An implementation for ladder */
public class GuidoLadder implements Ladder {

  /** The name of the ladder */
  @NonNull private final String name;

  /** The players per team in the ladder */
  private final int playersPerTeam;

  /** The base value of the ladder */
  private final int baseValue;

  /** The teams per match */
  private final int teamsPerMatch;

  /** The options for the ladder to make each ladder unique */
  @NonNull private final GuidoValuesMap options;

  /**
   * Create the ladder
   *
   * @param name the name of the ladder
   * @param playersPerTeam the players per team in the ladder
   * @param baseValue the base value of the ladder
   * @param teamsPerMatch the teams per match in the ladder
   * @param options the options of the ladder
   */
  public GuidoLadder(
      @NonNull String name,
      int playersPerTeam,
      int baseValue,
      int teamsPerMatch,
      @NonNull GuidoValuesMap options) {
    this.name = name;
    this.playersPerTeam = playersPerTeam;
    this.baseValue = baseValue;
    this.teamsPerMatch = teamsPerMatch;
    this.options = options;
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoLadder() {
    this("", 5, 500, -1, new GuidoValuesMap());
  }

  @Override
  public @NonNull Queue createQueue(@NonNull GuildData data) {
    String type = this.getOptions().getOr("type", String.class, "none");
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
  public @NonNull String getName() {
    return this.name;
  }

  @Override
  public @NonNull GuidoValuesMap getOptions() {
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

  /**
   * Get the number of teams per match
   *
   * @return the number of teams per match
   */
  @Override
  public int teamsPerMatch() {
    return 0;
  }
}
