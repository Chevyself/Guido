package me.googas.bot.core.matches.ladder;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.queue.Queue;
import me.googas.bot.core.matches.queue.GuidoPGMQueue;
import me.googas.bot.core.matches.queue.GuidoQueue;

/** An implementation for ladder */
public class GuidoLadder implements Ladder {

  @NonNull @Getter private final String name;
  private final int playersPerTeam;
  private final int baseValue;
  private final int teamsPerMatch;
  @NonNull @Getter private final Map<String, Map<String, Object>> information;

  /**
   * Create the ladder
   *
   * @param name the name of the ladder
   * @param playersPerTeam the players per team in the ladder
   * @param baseValue the base value of the ladder
   * @param teamsPerMatch the teams per match in the ladder
   * @param information the options of the ladder
   */
  public GuidoLadder(
      @NonNull String name,
      int playersPerTeam,
      int baseValue,
      int teamsPerMatch,
      @NonNull Map<String, Map<String, Object>> information) {
    this.name = name;
    this.playersPerTeam = playersPerTeam;
    this.baseValue = baseValue;
    this.teamsPerMatch = teamsPerMatch;
    this.information = information;
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoLadder() {
    this("", 5, 500, -1, new HashMap<>());
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
    String type = this.getString("global", "type", "none");
    switch (type) {
      case "pgm":
        return new GuidoPGMQueue(guildId, this.getName());
      default:
        return new GuidoQueue(guildId, this.getName());
    }
  }
}
