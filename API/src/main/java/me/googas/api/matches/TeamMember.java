package me.googas.api.matches;

import lombok.NonNull;
import me.googas.api.links.LinkableInfo;

/** Represents the linked info inside a team */
public interface TeamMember {

  /**
   * Get the linked information of a member
   *
   * @return the linked information
   */
  @NonNull
  LinkableInfo getLinkInfo();

  /**
   * Get the role of the member inside the team
   *
   * @return the role of the member
   */
  @NonNull
  TeamRole getTeamRole();
}
