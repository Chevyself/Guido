package dev.xevy.guido.mongo.types;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.MinecraftTeamSelectionType;
import me.googas.api.matches.ladder.Ladder;

public class MongoLadder implements Ladder {

  @NonNull @Getter private final String name;
  private final int playersPerTeam;
  private final int baseValue;
  private final int teamsPerMatch;
  @Getter private final double winMultiplier;
  @Getter private final double loseMultiplier;
  @Getter private final MinecraftTeamSelectionType teamSelectionType;

  public MongoLadder(
      @NonNull String name,
      int playersPerTeam,
      int baseValue,
      int teamsPerMatch,
      double winMultiplier,
      double loseMultiplier,
      MinecraftTeamSelectionType teamSelectionType) {
    this.name = name;
    this.playersPerTeam = playersPerTeam;
    this.baseValue = baseValue;
    this.teamsPerMatch = teamsPerMatch;
    this.winMultiplier = winMultiplier;
    this.loseMultiplier = loseMultiplier;
    this.teamSelectionType = teamSelectionType;
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

  public static class Document {
    public String name;
    public int playersPerTeam;
    public int baseValue;
    public int teamsPerMatch;
    public double winMultiplier;
    public double loseMultiplier;
    public MinecraftTeamSelectionType teamSelectionType;
  }
}
