package me.googas.api.immutable;

import java.util.Objects;
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

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ImmutableMinecraftMatchTeamMember that = (ImmutableMinecraftMatchTeamMember) o;
    return Objects.equals(id, that.id) && role == that.role;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, role);
  }

  @Override
  public String toString() {
    return "ImmutableMinecraftMatchTeamMember{" + "id=" + id + ", role=" + role + '}';
  }
}
