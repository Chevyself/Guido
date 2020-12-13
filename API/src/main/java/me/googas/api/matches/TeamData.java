package me.googas.api.matches;

import java.util.Collection;
import java.util.Map;
import lombok.NonNull;
import me.googas.api.lang.Localized;
import me.googas.api.links.Linkable;
import me.googas.commons.cache.Catchable;

/**
 * Team data is a team which can be saved to the database. TODO this should be named Team and {@link
 * Team} must be MatchTeam
 */
public interface TeamData extends Catchable, Localized, Queueable {

  /**
   * Add the member to the team
   *
   * @param member the member to add
   * @return whether the member was added
   */
  default boolean add(@NonNull TeamMember member) {
    return this.getMembers().add(member);
  }

  /**
   * Removes a member from the team
   *
   * @param member the member to remove
   * @return whether the user was removed
   */
  default boolean remove(@NonNull TeamMember member) {
    return this.getMembers()
        .removeIf(teamMember -> member.getLinkInfo().compare(teamMember.getLinkInfo()));
  }

  /**
   * Check whether the link is a member of this team
   *
   * @param linkable the link to check if it is a member of the team
   * @return true if it is in this team
   */
  default boolean contains(Linkable linkable) {
    if (linkable == null) return false;
    for (TeamMember member : this.getMembers()) {
      if (member.getLinkInfo().compare(linkable)) return true;
    }
    return false;
  }

  /**
   * Get the unique id of the team
   *
   * @return the id of the team as a string
   */
  @NonNull
  String getId();

  /**
   * Get the name of the team
   *
   * @return the name of the team
   */
  @NonNull
  String getName();

  /**
   * Get the members of the team
   *
   * @return the members of the team
   */
  @NonNull
  Collection<TeamMember> getMembers();

  @Override
  default void sendMessage(@NonNull String message) {
    for (TeamMember member : this.getMembers()) {
      Linkable link = member.getLinkInfo().getLink();
      if (link == null) return;
      link.sendMessage(message);
    }
  }

  @Override
  default void sendLocalized(@NonNull String key) {
    for (TeamMember member : this.getMembers()) {
      Linkable link = member.getLinkInfo().getLink();
      if (link == null) return;
      link.sendLocalized(key);
    }
  }

  @Override
  default void sendLocalized(@NonNull String key, @NonNull Map<String, String> placeholders) {
    for (TeamMember member : this.getMembers()) {
      Linkable link = member.getLinkInfo().getLink();
      if (link == null) return;
      link.sendLocalized(key, placeholders);
    }
  }
}
