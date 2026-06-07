package dev.xevy.guido.mongo.types;

import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.minecraft.MinecraftMatchTeam;
import me.googas.api.utility.ImmutableCollection;

public class MongoMinecraftMatchTeam implements MinecraftMatchTeam {

  @Getter private final int id;
  @NonNull private final Set<MongoMinecraftMatchTeamMember> members;
  @NonNull @Getter private final String name;

  public MongoMinecraftMatchTeam(
      int id, @NonNull Set<MongoMinecraftMatchTeamMember> members, @NonNull String name) {
    this.id = id;
    this.members = members;
    this.name = name;
  }

  @Override
  public @NonNull ImmutableCollection<MongoMinecraftMatchTeamMember> getMembers() {
    return new ImmutableCollection<>(members);
  }
}
