package dev.xevy.guido.mongo.types.mappers;

import dev.xevy.guido.mongo.types.MongoMinecraftMatchTeamMember;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;

public final class MinecraftMatchTeamMemberMapper {

  @NonNull
  public static MongoMinecraftMatchTeamMember fromDocument(
      @NonNull MongoMinecraftMatchTeamMember.Document doc) {
    return new MongoMinecraftMatchTeamMember(doc);
  }

  @NonNull
  public static Set<MongoMinecraftMatchTeamMember> fromDocuments(
      @NonNull Collection<MongoMinecraftMatchTeamMember.Document> docs) {
    Set<MongoMinecraftMatchTeamMember> members = new HashSet<>(docs.size());
    for (MongoMinecraftMatchTeamMember.Document doc : docs) {
      members.add(MinecraftMatchTeamMemberMapper.fromDocument(doc));
    }
    return members;
  }
}
