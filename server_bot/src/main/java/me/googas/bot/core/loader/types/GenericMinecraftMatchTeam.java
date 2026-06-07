package me.googas.bot.core.loader.types;

import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.minecraft.MinecraftMatchTeam;
import me.googas.api.utility.ImmutableCollection;

public class GenericMinecraftMatchTeam implements MinecraftMatchTeam {

  @Getter private final int id;
  @NonNull private final Set<GenericMinecraftMatchTeamMember> members;
  @NonNull @Getter private final String name;

  public GenericMinecraftMatchTeam(
      int id, @NonNull Set<GenericMinecraftMatchTeamMember> members, @NonNull String name) {
    this.id = id;
    this.members = members;
    this.name = name;
  }

  @Override
  public @NonNull ImmutableCollection<GenericMinecraftMatchTeamMember> getMembers() {
    return new ImmutableCollection<>(members);
  }
}
