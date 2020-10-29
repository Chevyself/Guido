package me.googas.api.client.data;

import me.googas.api.matches.Team;
import me.googas.api.matches.TeamMember;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/** An implementation for teams */
public class TeamImpl implements Team {

  /** The name of the team */
  @NotNull private final String name;

  /** The members of the team */
  @NotNull private final Set<TeamMember> members;

  /**
   * Create the team
   *
   * @param name the name of the team
   * @param members the members of the team
   */
  public TeamImpl(@NotNull String name, @NotNull Set<TeamMember> members) {
    this.name = name;
    this.members = members;
  }

  /** @deprecated this constructor may only be used by gson */
  public TeamImpl() {
    this("", new HashSet<>());
  }

  @Override
  public @NotNull Collection<TeamMember> getMembers() {
    return this.members;
  }

  @Override
  public @NotNull String getName() {
    return this.name;
  }
}
