package dev.xevy.guido.mongo.types.mappers;

import dev.xevy.guido.mongo.types.MongoMinecraftMatchTeam;
import lombok.NonNull;
import me.googas.api.matches.minecraft.MinecraftMatchTeam;

public final class MinecraftMatchTeamMapper {

  @NonNull
  public static MongoMinecraftMatchTeam fromDocument(
      @NonNull MongoMinecraftMatchTeam.Document document) {
    return new MongoMinecraftMatchTeam(document);
  }

  @NonNull
  public static MongoMinecraftMatchTeam.Document toDocument(@NonNull MinecraftMatchTeam team) {
    MongoMinecraftMatchTeam.Document doc = new MongoMinecraftMatchTeam.Document();
    doc.id = team.getId();
    doc.name = team.getName();
    doc.members = MinecraftMatchTeamMemberMapper.toDocuments(team.getMembers());
    doc.pgmPartyId = team.getPgmPartyId().orElse(null);
    return doc;
  }
}
