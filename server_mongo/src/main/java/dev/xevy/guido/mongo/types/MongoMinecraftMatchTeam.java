package dev.xevy.guido.mongo.types;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.minecraft.MinecraftMatchTeam;
import me.googas.api.utility.ImmutableCollection;

public class MongoMinecraftMatchTeam implements MinecraftMatchTeam {

  @NonNull private final Document document;

  public MongoMinecraftMatchTeam(@NonNull Document document) {
    this.document = document;
  }

  @Override
  public int getId() {
    return this.document.id;
  }

  @Override
  public @NonNull ImmutableCollection<MongoMinecraftMatchTeamMember> getMembers() {
    return ImmutableCollection.map(this.document.members, MongoMinecraftMatchTeamMember::new);
  }

  @Override
  public @NonNull String getName() {
    return this.document.name;
  }

  @Override
  public @NonNull Optional<String> getPgmPartyId() {
    return Optional.ofNullable(this.document.pgmPartyId);
  }

  public static class Document {
    public int id = MatchTeam.NO_TEAM;
    @NonNull public Set<MongoMinecraftMatchTeamMember.Document> members = new HashSet<>();
    @NonNull public String name = "";
    public String pgmPartyId = null;
  }
}
