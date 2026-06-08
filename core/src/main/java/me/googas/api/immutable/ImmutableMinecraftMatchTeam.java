package me.googas.api.immutable;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.minecraft.MinecraftMatchTeam;
import me.googas.api.utility.ImmutableCollection;

public class ImmutableMinecraftMatchTeam implements MinecraftMatchTeam {

  @Getter private final int id;
  @NonNull private final Set<ImmutableMinecraftMatchTeamMember> members;
  @NonNull @Getter private final String name;

  public ImmutableMinecraftMatchTeam(
      int id, @NonNull Set<ImmutableMinecraftMatchTeamMember> members, @NonNull String name) {
    this.id = id;
    this.members = members;
    this.name = name;
  }

  public ImmutableMinecraftMatchTeam(@NonNull MinecraftMatchTeam team) {
    this(team.getId(), ImmutableMinecraftMatchTeamMember.from(team.getMembers()), team.getName());
  }

  public static @NonNull List<ImmutableMinecraftMatchTeam> from(
      @NonNull ImmutableCollection<? extends MinecraftMatchTeam> teams) {
    return teams.stream().map(ImmutableMinecraftMatchTeam::new).collect(Collectors.toList());
  }

  @Override
  public @NonNull ImmutableCollection<ImmutableMinecraftMatchTeamMember> getMembers() {
    return new ImmutableCollection<>(members);
  }

  @Override
  public String toString() {
    return "ImmutableMinecraftMatchTeam{"
        + "id="
        + id
        + ", members="
        + members
        + ", name='"
        + name
        + '\''
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ImmutableMinecraftMatchTeam that = (ImmutableMinecraftMatchTeam) o;
    return id == that.id
        && Objects.equals(members, that.members)
        && Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, members, name);
  }
}
