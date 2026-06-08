package dev.xevy.guido.mongo.types.mappers;

import dev.xevy.guido.mongo.types.MongoMinecraftMatchTeam;
import lombok.NonNull;

public final class MinecraftMatchTeamMapper {

  @NonNull
  public static MongoMinecraftMatchTeam fromDocument(
      @NonNull MongoMinecraftMatchTeam.Document document) {
    return new MongoMinecraftMatchTeam(
        document.id, MinecraftMatchTeamMemberMapper.fromDocuments(document.members), document.name);
  }
}
