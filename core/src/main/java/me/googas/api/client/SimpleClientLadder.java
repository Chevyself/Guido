package me.googas.api.client;

import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.MinecraftTeamSelectionType;
import me.googas.api.matches.ladder.Ladder;

public class SimpleClientLadder implements Ladder {

  private final int playersPerTeam;
  private final int baseValue;
  private final int teamsPerMatch;
  @NonNull @Getter private final String name;
  @NonNull @Getter private final MinecraftTeamSelectionType teamSelectionType;

  public SimpleClientLadder(
      int playersPerTeam,
      int baseValue,
      int teamsPerMatch,
      @NonNull String name,
      @NonNull Map<String, Map<String, Object>> information,
      @NonNull MinecraftTeamSelectionType teamSelectionType) {
    this.playersPerTeam = playersPerTeam;
    this.baseValue = baseValue;
    this.teamsPerMatch = teamsPerMatch;
    this.name = name;
    this.teamSelectionType = teamSelectionType;
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
}
