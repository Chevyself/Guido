package dev.xevy.guido.mongo.types;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.MinecraftTeamSelectionType;
import me.googas.api.matches.ladder.Ladder;

public class MongoLadder implements Ladder {

  @NonNull @Getter private final Document document;

  public MongoLadder(@NonNull Document document) {
    this.document = document;
  }

  @Override
  public int playersPerTeam() {
    return this.document.playersPerTeam;
  }

  @Override
  public int baseValue() {
    return this.document.baseValue;
  }

  @Override
  public int teamsPerMatch() {
    return this.document.teamsPerMatch;
  }

  @Override
  public @NonNull String getName() {
    return this.document.name;
  }

  @Override
  public @NonNull MinecraftTeamSelectionType getTeamSelectionType() {
    return this.document.teamSelectionType;
  }

  @Override
  public double getWinMultiplier() {
    return this.document.winMultiplier;
  }

  @Override
  public double getLoseMultiplier() {
    return this.document.loseMultiplier;
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
