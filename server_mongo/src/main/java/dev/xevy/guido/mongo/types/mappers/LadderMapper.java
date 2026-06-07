package dev.xevy.guido.mongo.types.mappers;

import dev.xevy.guido.mongo.types.MongoGuidoGuild;
import dev.xevy.guido.mongo.types.MongoLadder;
import lombok.NonNull;
import me.googas.api.matches.ladder.Ladder;

public final class LadderMapper {
  @NonNull
  public static MongoLadder fromDocument(@NonNull MongoGuidoGuild.LadderDocument document) {
    return new MongoLadder(
        document.name,
        document.playersPerTeam,
        document.baseValue,
        document.teamsPerMatch,
        document.winMultiplier,
        document.loseMultiplier,
        document.teamSelectionType);
  }

  @NonNull
  public static MongoGuidoGuild.LadderDocument toDocument(@NonNull Ladder ladder) {
    MongoGuidoGuild.LadderDocument document = new MongoGuidoGuild.LadderDocument();
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
