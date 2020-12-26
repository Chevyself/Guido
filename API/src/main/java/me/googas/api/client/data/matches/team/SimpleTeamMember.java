package me.googas.api.client.data.matches.team;

import java.util.Objects;
import lombok.NonNull;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.client.data.links.SimpleLinkableInfo;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.matches.team.TeamMember;
import me.googas.api.matches.team.TeamRole;
import me.googas.commons.builder.ToStringBuilder;

public class SimpleTeamMember implements TeamMember {

  @NonNull private final LinkableInfo linkInfo;
  @NonNull private final TeamRole role;

  /**
   * Create the team member
   *
   * @param linkInfo the info of the team
   * @param role the role of the team
   */
  public SimpleTeamMember(@NonNull LinkableInfo linkInfo, @NonNull TeamRole role) {
    this.linkInfo = linkInfo;
    this.role = role;
  }

  /** @deprecated constructor may only be used in gson */
  public SimpleTeamMember() {
    this(new SimpleLinkableInfo(LinkableType.NONE, new SimpleValuesMap()), TeamRole.NORMAL);
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
  public String toString() {
    return new ToStringBuilder(this)
        .append("linkInfo", this.linkInfo)
        .append("role", this.role)
        .build();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || this.getClass() != object.getClass()) return false;
    SimpleTeamMember that = (SimpleTeamMember) object;
    return this.linkInfo.equals(that.linkInfo) && this.role == that.role;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.linkInfo, this.role);
  }
}
