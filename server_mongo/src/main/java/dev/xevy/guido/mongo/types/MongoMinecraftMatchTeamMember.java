package dev.xevy.guido.mongo.types;

import java.util.UUID;
import lombok.NonNull;
import me.googas.api.matches.minecraft.MinecraftMatchTeamMember;
import me.googas.api.matches.team.TeamRole;

public class MongoMinecraftMatchTeamMember implements MinecraftMatchTeamMember {

  @NonNull private final Document document;

  public MongoMinecraftMatchTeamMember(@NonNull Document document) {
    this.document = document;
  }

  @Override
  public @NonNull UUID getId() {
    return this.document.id;
  }

  @Override
  public @NonNull TeamRole getRole() {
    return this.document.role;
  }

  public static class Document {
    @NonNull public UUID id = UUID.randomUUID();
    @NonNull public TeamRole role = TeamRole.MEMBER;
  }
}
