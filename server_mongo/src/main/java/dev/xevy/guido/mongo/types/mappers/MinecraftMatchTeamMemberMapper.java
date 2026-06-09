package dev.xevy.guido.mongo.types.mappers;

import dev.xevy.guido.mongo.types.MongoMinecraftMatchTeamMember;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.matches.minecraft.MinecraftMatchTeamMember;
import me.googas.api.utility.ImmutableCollection;

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

  public static @NonNull Set<MongoMinecraftMatchTeamMember.Document> toDocuments(
      @NonNull ImmutableCollection<? extends MinecraftMatchTeamMember> members) {
    Set<MongoMinecraftMatchTeamMember.Document> docs = new HashSet<>(members.size());
    for (MinecraftMatchTeamMember member : members) {
      docs.add(MinecraftMatchTeamMemberMapper.toDocument(member));
    }
    return docs;
  }

  private static @NonNull MongoMinecraftMatchTeamMember.Document toDocument(
      @NonNull MinecraftMatchTeamMember member) {
    MongoMinecraftMatchTeamMember.Document doc = new MongoMinecraftMatchTeamMember.Document();
    doc.id = member.getId();
    doc.role = member.getRole();
    return doc;
  }
}
