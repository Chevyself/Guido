package com.starfishst.bot.handlers.data;

import com.starfishst.guido.api.data.matches.Ladder;
import org.jetbrains.annotations.NotNull;

/** An implementation for ladder */
public class GuidoLadder implements Ladder {

  /** The name of the ladder */
  @NotNull private final String name;

  /** The players per team in the ladder */
  private int playersPerTeam;

  /** The base value of the ladder */
  private int baseValue;

  /**
   * Create the ladder
   *
   * @param name the name of the ladder
   * @param playersPerTeam the players per team in the ladder
   * @param baseValue the base value of the ladder
   */
  public GuidoLadder(@NotNull String name, int playersPerTeam, int baseValue) {
    this.name = name;
    this.playersPerTeam = playersPerTeam;
    this.baseValue = baseValue;
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoLadder() {
    this("", 5, 500);
  }

  /**
   * Get the name of the ladder
   *
   * @return the name of the ladder
   */
  @Override
  public @NotNull String getName() {
    return this.name;
  }

  /**
   * Get the number of players per team
   *
   * @return the number of players per team
   */
  @Override
  public int playersPerTeam() {
    return this.playersPerTeam;
  }

  /**
   * Get the base value which all the players start with
   *
   * @return the base value
   */
  @Override
  public int baseValue() {
    return this.baseValue;
  }
}
