package me.googas.api.matches.ladder;

import lombok.NonNull;
import me.googas.api.matches.MinecraftTeamSelectionType;

/** The global ladder cannot be edited, this ladder calculates the global elo of the linked data */
public class GlobalLadder implements Ladder {

  /** A public static instance for global ladders */
  public static final Ladder INSTANCE = new GlobalLadder();

  @Override
  public int playersPerTeam() {
    return 0;
  }

  @Override
  public int baseValue() {
    return 0;
  }

  @Override
  public int teamsPerMatch() {
    return 0;
  }

  @Override
  public @NonNull String getName() {
    return "global";
  }

  @Override
  public @NonNull MinecraftTeamSelectionType getTeamSelectionType() {
    return MinecraftTeamSelectionType.RANDOM;
  }

  @Override
  public double getWinMultiplier() {
    return 1;
  }

  @Override
  public double getLoseMultiplier() {
    return 1;
  }
}
