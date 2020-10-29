package me.googas.api.matches;

import me.googas.api.links.LinkedInfo;
import org.jetbrains.annotations.NotNull;

/** Represents the linked info inside a team */
public interface TeamMember {

  /**
   * Get the linked information of a member
   *
   * @return the linked information
   */
  @NotNull
  LinkedInfo getLinkInfo();

  /**
   * Get the role of the member inside the team
   *
   * @return the role of the member
   */
  @NotNull
  TeamRole getTeamRole();
}
