package me.googas.bot.core.types;

import me.googas.api.links.LinkedDataType;
import me.googas.api.links.LinkedInfo;
import me.googas.api.matches.TeamMember;
import me.googas.api.matches.TeamRole;
import me.googas.bot.core.types.maps.GuidoValuesMap;
import org.jetbrains.annotations.NotNull;

/** Implementation for team member */
public class GuidoTeamMember implements TeamMember {

  /** The information of the team */
  @NotNull private final LinkedInfo linkInfo;

  /** The role of the member in the team */
  @NotNull private final TeamRole role;

  /**
   * Create the team member
   *
   * @param linkInfo the info of the team
   * @param role the role of the team
   */
  public GuidoTeamMember(@NotNull LinkedInfo linkInfo, @NotNull TeamRole role) {
    this.linkInfo = linkInfo;
    this.role = role;
  }

  /** @deprecated constructor may only be used in gson */
  public GuidoTeamMember() {
    this(new GuidoLinkedInfo(LinkedDataType.NONE, new GuidoValuesMap()), TeamRole.NORMAL);
  }

  @Override
  public @NotNull LinkedInfo getLinkInfo() {
    return this.linkInfo;
  }

  @Override
  public @NotNull TeamRole getTeamRole() {
    return this.role;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GuidoTeamMember)) return false;

    GuidoTeamMember that = (GuidoTeamMember) o;

    return this.linkInfo.equals(that.linkInfo);
  }

  @Override
  public int hashCode() {
    return this.linkInfo.hashCode();
  }
}
