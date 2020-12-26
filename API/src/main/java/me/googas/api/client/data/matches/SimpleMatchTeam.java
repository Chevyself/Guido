package me.googas.api.client.data.matches;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.team.TeamMember;
import me.googas.commons.builder.ToStringBuilder;

public class SimpleMatchTeam implements MatchTeam {

  private final int id;
  @NonNull private final String name;
  @NonNull private final Set<TeamMember> members;

  /**
   * Create the team
   *
   * @param id the id of the team
   * @param name the name of the team
   * @param members the members of the team
   */
  public SimpleMatchTeam(int id, @NonNull String name, @NonNull Set<TeamMember> members) {
    this.id = id;
    this.name = name;
    this.members = members;
  }

  /** @deprecated this constructor may only be used by gson */
  public SimpleMatchTeam() {
    this(-1, "", new HashSet<>());
  }

  @Override
  public int getId() {
    return this.id;
  }

  @Override
  public @NonNull Collection<TeamMember> getMembers() {
    return this.members;
  }

  @Override
  public @NonNull String getName() {
    return this.name;
  }

  @Override
  public boolean add(@NonNull TeamMember member) {
    return false;
  }

  @Override
  public boolean remove(@NonNull TeamMember member) {
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
    SimpleMatchTeam that = (SimpleMatchTeam) object;
    return this.id == that.id && this.name.equals(that.name) && this.members.equals(that.members);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.name, this.members);
  }
}
