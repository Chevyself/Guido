package me.googas.api.client.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import me.googas.api.matches.Team;
import me.googas.api.matches.TeamMember;
import org.jetbrains.annotations.NotNull;

/** An implementation for teams */
public class TeamImpl implements Team {

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
  public TeamImpl(int id, @NotNull String name, @NotNull Set<TeamMember> members) {
    this.id = id;
    this.name = name;
    this.members = members;
  }

  /** @deprecated this constructor may only be used by gson */
  public TeamImpl() {
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
  public boolean addMember(@NotNull TeamMember member) {
    return false;
  }

  @Override
  public boolean removeMember(@NotNull TeamMember member) {
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TeamImpl)) return false;

    TeamImpl team = (TeamImpl) o;

    if (this.id != team.id) return false;
    return this.name.equals(team.name);
  }

  @Override
  public int hashCode() {
    int result = this.id;
    result = 31 * result + this.name.hashCode();
    return result;
  }
}
