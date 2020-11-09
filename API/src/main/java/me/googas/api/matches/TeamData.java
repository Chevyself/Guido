package me.googas.api.matches;

import java.util.Collection;
import me.googas.api.lang.Localized;
import me.googas.commons.cache.Catchable;
import org.jetbrains.annotations.NotNull;

/** Team data is a team which can be saved to the database. */
public interface TeamData extends Catchable, Localized {

  /**
   * Add the member to the team
   *
   * @param member the member to add
   * @return whether the member was added
   */
  default boolean add(@NotNull TeamMember member) {
    return this.getMembers().add(member);
  }

  /**
   * Removes a member from the team
   *
   * @param member the member to remove
   * @return whether the user was removed
   */
  default boolean remove(@NotNull TeamMember member) {
    return this.getMembers().remove(member);
  }

  /**
   * Get the name of the team
   *
   * @return the name of the team
   */
  @NotNull
  String getName();

  /**
   * Get the members of the team
   *
   * @return the members of the team
   */
  @NotNull
  Collection<TeamMember> getMembers();
}
