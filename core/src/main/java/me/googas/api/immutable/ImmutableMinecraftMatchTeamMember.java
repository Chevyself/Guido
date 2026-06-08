package me.googas.api.immutable;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.minecraft.MinecraftMatchTeamMember;
import me.googas.api.matches.team.TeamRole;
import me.googas.api.utility.ImmutableCollection;

public class ImmutableMinecraftMatchTeamMember implements MinecraftMatchTeamMember {

  @NonNull @Getter private final UUID id;
  @NonNull @Getter private final TeamRole role;

  public ImmutableMinecraftMatchTeamMember(@NonNull UUID id, @NonNull TeamRole role) {
    this.id = id;
    this.role = role;
  }

  public ImmutableMinecraftMatchTeamMember(@NonNull MinecraftMatchTeamMember member) {
    this(member.getId(), member.getRole());
  }

  public static @NonNull Set<ImmutableMinecraftMatchTeamMember> from(
      @NonNull ImmutableCollection<? extends MinecraftMatchTeamMember> members) {
    return members.stream().map(ImmutableMinecraftMatchTeamMember::new).collect(Collectors.toSet());
  }
}
