package me.googas.bot.core.matches.team;

import lombok.NonNull;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.matches.team.TeamMember;
import me.googas.api.matches.team.TeamRole;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.bot.core.links.GuidoLinkableInfo;

/** Implementation for team member */
public class GuidoTeamMember implements TeamMember {

  @NonNull private final LinkableInfo linkInfo;
  @NonNull private final TeamRole role;

  /**
   * Create the team member
   *
   * @param linkInfo the info of the team
   * @param role the role of the team
   */
  public GuidoTeamMember(@NonNull LinkableInfo linkInfo, @NonNull TeamRole role) {
    this.linkInfo = linkInfo;
    this.role = role;
  }

  /** @deprecated constructor may only be used in gson */
  public GuidoTeamMember() {
    this(new GuidoLinkableInfo(LinkableType.NONE, new GuidoValuesMap()), TeamRole.NORMAL);
  }

  @Override
  public @NonNull LinkableInfo getLinkInfo() {
    return this.linkInfo;
  }

  @Override
  public @NonNull TeamRole getTeamRole() {
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
