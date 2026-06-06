package me.googas.api.matches.ladder;

import lombok.NonNull;
import me.googas.api.matches.MinecraftTeamSelectionType;

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
   * Get the number of teams per match
   *
   * @return the number of teams per match
   */
  int teamsPerMatch();

  /**
   * Get the name of the ladder
   *
   * @return the name of the ladder
   */
  @NonNull
  String getName();

  @NonNull
  MinecraftTeamSelectionType getTeamSelectionType();

  double getWinMultiplier();

  double getLoseMultiplier();
}
