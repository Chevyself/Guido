package com.starfishst.guido.api.data.matches;

import com.starfishst.guido.api.data.links.LinkedData;
import com.starfishst.guido.api.data.links.LinkedInfo;
import java.util.Map;
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
    for (LinkedInfo<?, ?> linkedInfo : this.getMembers().keySet()) {
      LinkedData<?, ?> data = linkedInfo.getData();
      if (data != null) {
        sum += data.getElo(ladder);
        size++;
      }
    }
    return sum / size;
  }

  /**
   * Get the members of the team
   *
   * @return the members of the team
   */
  @NotNull
  Map<? extends LinkedInfo<?, ?>, TeamRole> getMembers();

  /**
   * Get the name of the team
   *
   * @return the name of the team
   */
  @NotNull
  String getName();
}
