package com.starfishst.bot.handlers.data.types;

import me.googas.api.matches.Team;
import me.googas.api.matches.TeamMember;
import java.util.Collection;
import java.util.Set;
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
}
