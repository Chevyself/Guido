package com.starfishst.guido.api.data.matches;

import org.jetbrains.annotations.NotNull;

/** A ladder is a ranked system that users may use to climb */
public interface Ladder {

  /**
   * Get the number of players per team
   *
   * @return the number of players per team
   */
  int playersPerTeam();

  /**
   * Get the base value which all the players start with
   *
   * @return the base value
   */
  int baseValue();

  /**
   * Get the name of the ladder
   *
   * @return the name of the ladder
   */
  @NotNull
  String getName();
}
