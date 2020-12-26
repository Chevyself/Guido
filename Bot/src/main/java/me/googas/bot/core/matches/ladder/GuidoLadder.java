package me.googas.bot.core.matches.ladder;

import lombok.NonNull;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.queue.Queue;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.bot.core.matches.queue.GuidoPGMQueue;
import me.googas.bot.core.matches.queue.GuidoQueue;

/** An implementation for ladder */
public class GuidoLadder implements Ladder {

  @NonNull private final String name;
  private final int playersPerTeam;
  private final int baseValue;
  private final int teamsPerMatch;
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
    String type = this.getOptions().getOr("type", String.class, "none");
    switch (type) {
      case "pgm":
        return new GuidoPGMQueue(guildId, this.getName());
      default:
        return new GuidoQueue(guildId, this.getName());
    }
  }
}
