package me.googas.api.client.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import me.googas.api.matches.Team;
import me.googas.api.matches.TeamMember;
import me.googas.commons.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;

/** An implementation for teams */
public class SimpleTeam implements Team {

  /** The id of the team */
  private final int id;
  /** The name of the team */
  @NotNull private final String name;

  /** The members of the team */
  @NotNull private final Set<TeamMember> members;

  /**
   * Create the team
   *
   * @param id the id of the team
   * @param name the name of the team
   * @param members the members of the team
   */
  public SimpleTeam(int id, @NotNull String name, @NotNull Set<TeamMember> members) {
    this.id = id;
    this.name = name;
    this.members = members;
  }

  /** @deprecated this constructor may only be used by gson */
  public SimpleTeam() {
    this(-1, "", new HashSet<>());
  }

  @Override
  public int getId() {
    return this.id;
  }

  @Override
  public @NotNull Collection<TeamMember> getMembers() {
    return this.members;
  }

  @Override
  public @NotNull String getName() {
    return this.name;
  }

  @Override
  public boolean add(@NotNull TeamMember member) {
    return false;
  }

  @Override
  public boolean remove(@NotNull TeamMember member) {
    return false;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", this.id)
        .append("name", this.name)
        .append("members", this.members)
        .build();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || this.getClass() != object.getClass()) return false;
    SimpleTeam that = (SimpleTeam) object;
    return this.id == that.id && this.name.equals(that.name) && this.members.equals(that.members);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.name, this.members);
  }
}
