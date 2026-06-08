package dev.xevy.guido.mongo.types.mappers;

import dev.xevy.guido.mongo.types.MongoLadder;
import lombok.NonNull;
import me.googas.api.matches.ladder.Ladder;

public final class LadderMapper {
  @NonNull
  public static MongoLadder fromDocument(@NonNull MongoLadder.Document document) {
    return new MongoLadder(document);
  }

  @NonNull
  public static MongoLadder.Document toDocument(@NonNull Ladder ladder) {
    MongoLadder.Document document = new MongoLadder.Document();
    document.name = ladder.getName();
    document.playersPerTeam = ladder.playersPerTeam();
    document.baseValue = ladder.baseValue();
    document.teamsPerMatch = ladder.teamsPerMatch();
    document.winMultiplier = ladder.getWinMultiplier();
    document.loseMultiplier = ladder.getLoseMultiplier();
    document.teamSelectionType = ladder.getTeamSelectionType();
    return document;
  }
}
