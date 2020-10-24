package com.starfishst.bot.handlers.data;

import com.starfishst.guido.api.data.links.LinkedInfo;
import com.starfishst.guido.api.data.matches.Team;
import com.starfishst.guido.api.data.matches.TeamRole;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/** An implementation for team */
public class GuidoTeam implements Team {

  /** The members of the team */
  @NotNull private final Map<LinkedInfo, TeamRole> members;

  /** The name of the team */
  @NotNull private final String name;

  /**
   * Create the team
   *
   * @param members the members inside the team
   * @param name the name of the team
   */
  public GuidoTeam(@NotNull Map<LinkedInfo, TeamRole> members, @NotNull String name) {
    this.members = members;
    this.name = name;
  }

  @Override
  public @NotNull String getName() {
    return this.name;
  }

  @Override
  public @NotNull Map<LinkedInfo, TeamRole> getMembers() {
    return this.members;
  }
}
