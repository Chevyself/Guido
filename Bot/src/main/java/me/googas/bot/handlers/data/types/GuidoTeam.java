package me.googas.bot.handlers.data.types;

import java.util.Collection;
import java.util.Set;
import me.googas.api.matches.Team;
import me.googas.api.matches.TeamMember;
import org.jetbrains.annotations.NotNull;

/** An implementation for team */
public class GuidoTeam implements Team {

  /** The members of the team */
  @NotNull private final Set<TeamMember> members;

  /** The name of the team */
  @NotNull private final String name;

  /**
   * Create the team
   *
   * @param members the members inside the team
   * @param name the name of the team
   */
  public GuidoTeam(@NotNull Set<TeamMember> members, @NotNull String name) {
    this.members = members;
    this.name = name;
  }

  @Override
  public @NotNull String getName() {
    return this.name;
  }

  @Override
  public @NotNull Collection<TeamMember> getMembers() {
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

    if (!this.members.equals(guidoTeam.members)) return false;
    return this.name.equals(guidoTeam.name);
  }

  @Override
  public int hashCode() {
    int result = this.members.hashCode();
    result = 31 * result + this.name.hashCode();
    return result;
  }
}
