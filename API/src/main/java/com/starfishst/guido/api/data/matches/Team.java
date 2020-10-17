package com.starfishst.guido.api.data.matches;

import com.starfishst.guido.api.data.UserData;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/** This object represents a team. Which is basically a collection of members */
public interface Team {

  /**
   * Get the members of the team
   *
   * @return the members of the team
   */
  @NotNull
  Map<? extends UserData, TeamRole> getMembers();
}
