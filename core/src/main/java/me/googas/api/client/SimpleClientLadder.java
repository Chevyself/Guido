package me.googas.api.client;

import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.queue.MinecraftQueue;

public class SimpleClientLadder implements Ladder {

  private final int playersPerTeam;
  private final int baseValue;
  private final int teamsPerMatch;
  @NonNull @Getter private final String name;
  @NonNull @Getter private final Map<String, Map<String, Object>> information;

  public SimpleClientLadder(
      int playersPerTeam,
      int baseValue,
      int teamsPerMatch,
      @NonNull String name,
      @NonNull Map<String, Map<String, Object>> information) {
    this.playersPerTeam = playersPerTeam;
    this.baseValue = baseValue;
    this.teamsPerMatch = teamsPerMatch;
    this.name = name;
    this.information = information;
  }

  @Override
  public int playersPerTeam() {
    return playersPerTeam;
  }

  @Override
  public int baseValue() {
    return baseValue;
  }

  @Override
  public int teamsPerMatch() {
    return teamsPerMatch;
  }

  @Override
  public @NonNull MinecraftQueue createQueue(long guildId) {
    throw new UnsupportedOperationException("Cannot create queues from simple ladders");
  }
}
