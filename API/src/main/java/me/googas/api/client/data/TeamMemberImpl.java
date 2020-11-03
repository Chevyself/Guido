package me.googas.api.client.data;

import me.googas.api.links.LinkedDataType;
import me.googas.api.links.LinkedInfo;
import me.googas.api.matches.TeamMember;
import me.googas.api.matches.TeamRole;
import org.jetbrains.annotations.NotNull;

/** Implementation for team member */
public class TeamMemberImpl implements TeamMember {

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
  public TeamMemberImpl(@NotNull LinkedInfo linkInfo, @NotNull TeamRole role) {
    this.linkInfo = linkInfo;
    this.role = role;
  }

  /** @deprecated constructor may only be used in gson */
  public TeamMemberImpl() {
    this(new LinkedInfoImpl(LinkedDataType.NONE, new ValuesMapImpl()), TeamRole.NORMAL);
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
    if (!(o instanceof TeamMemberImpl)) return false;

    TeamMemberImpl that = (TeamMemberImpl) o;

    if (!this.linkInfo.equals(that.linkInfo)) return false;
    return this.role == that.role;
  }

  @Override
  public int hashCode() {
    int result = this.linkInfo.hashCode();
    result = 31 * result + this.role.hashCode();
    return result;
  }
}
