package com.starfishst.guido.api.data.matches;

import com.starfishst.guido.api.data.links.LinkedData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
}
