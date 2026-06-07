package me.googas.api.client;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.MinecraftTeamSelectionType;
import me.googas.api.matches.ladder.Ladder;

public class SimpleClientLadder implements Ladder {

  private final int playersPerTeam;
  private final int baseValue;
  private final int teamsPerMatch;
  @Getter private final double winMultiplier;
  @Getter private final double loseMultiplier;
  @NonNull @Getter private final String name;
  @NonNull @Getter private final MinecraftTeamSelectionType teamSelectionType;

  public SimpleClientLadder(
      int playersPerTeam,
      int baseValue,
      int teamsPerMatch,
      double winMultiplier,
      double loseMultiplier,
      @NonNull String name,
      @NonNull MinecraftTeamSelectionType teamSelectionType) {
    this.playersPerTeam = playersPerTeam;
    this.baseValue = baseValue;
    this.teamsPerMatch = teamsPerMatch;
    this.winMultiplier = winMultiplier;
    this.name = name;
    this.loseMultiplier = loseMultiplier;
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
