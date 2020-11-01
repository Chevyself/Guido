package me.googas.api.matches;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.googas.api.links.LinkedData;
import org.jetbrains.annotations.NotNull;

/** This object represents a team. Which is basically a collection of members */
public interface Team {

  /**
   * Calculate the average elo for a team in a ladder
   *
   * @param ladder the ladder to calculate the elo to
   * @return the average elo of the team
   */
  default float getElo(@NotNull Ladder ladder) {
    float sum = 0;
    int size = 0;
    for (TeamMember member : this.getMembers()) {
      LinkedData data = member.getLinkInfo().getLink();
      if (data != null) {
        sum += data.refresh().getElo(ladder);
        size++;
      }
    }
    return sum / size;
  }

  /**
   * Get the single identification for all the members
   *
   * @return the single identification
   */
  @NotNull
  default Collection<String> getMemberSingles() {
    List<String> singles = new ArrayList<>();
    for (TeamMember member : this.getMembers()) {
      LinkedData data = member.getLinkInfo().getLink();
      if (data != null) {
        singles.add(data.getSingle());
      }
    }
    return singles;
  }

  /**
   * Adds a member to the team
   *
   * @param member the member to add
   * @return whether the member was added to the team
   */
  boolean addMember(@NotNull TeamMember member);

  /**
   * Get the members of the team
   *
   * @return the members of the team
   */
  @NotNull
  Collection<TeamMember> getMembers();

  /**
   * Get the name of the team
   *
   * @return the name of the team
   */
  @NotNull
  String getName();

  /**
   * Removes a member from the team
   *
   * @param member the member to remove
   * @return whether the member was removed from the team
   */
  boolean removeMember(@NotNull TeamMember member);

  /**
   * Get an unique way to identify the team
   *
   * @return the unique way to identify the team
   */
  int getId();
}
