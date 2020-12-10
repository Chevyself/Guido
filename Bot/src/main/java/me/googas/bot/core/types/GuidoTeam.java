package me.googas.bot.core.types;

import java.util.Collection;
import lombok.NonNull;
import me.googas.api.matches.Team;
import me.googas.api.matches.TeamMember;

/** An implementation for team */
public class GuidoTeam implements Team {

  /** The id of the team */
  private final int id;

  /** The name of the team */
  @NonNull private final String name;

  /** The members of the team */
  @NonNull private final Collection<TeamMember> members;

  /**
   * Create the team
   *
   * @param id the id of the team
   * @param members the members inside the team
   * @param name the name of the team
   */
  public GuidoTeam(int id, @NonNull Collection<TeamMember> members, @NonNull String name) {
    this.id = id;
    this.members = members;
    this.name = name;
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
  public int getId() {
    return this.id;
  }

  @Override
  public @NonNull Collection<TeamMember> getMembers() {
    return this.members;
  }

  @Override
  public String toString() {
    return "GuidoTeam{" + "members=" + this.members + ", name='" + this.name + '\'' + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GuidoTeam)) return false;

    GuidoTeam guidoTeam = (GuidoTeam) o;

    if (this.id != guidoTeam.id) return false;
    return this.name.equals(guidoTeam.name);
  }

  @Override
  public int hashCode() {
    int result = this.id;
    result = 31 * result + this.name.hashCode();
    return result;
  }
}
