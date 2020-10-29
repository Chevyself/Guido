package com.starfishst.bot.handlers.data.types;

import me.googas.api.RankRange;
import org.jetbrains.annotations.NotNull;

/** Rank range implementation for the guido bot */
public class GuidoRankRange implements RankRange {

  /** The ladder that is using this range */
  @NotNull private final String ladder;

  /** The minimum value of the range */
  private final int min;

  /** The maximum value of the range */
  private final int max;

  /**
   * Create the range
   *
   * @param ladder the ladder that will use this range
   * @param min the minimum value of the range
   * @param max the maximum value of the range
   */
  public GuidoRankRange(@NotNull String ladder, int min, int max) {
    this.ladder = ladder;
    this.min = min;
    this.max = max;
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoRankRange() {
    this("", -1, -1);
  }

  @Override
  public int getMin() {
    return this.min;
  }

  @Override
  public int getMax() {
    return this.max;
  }

  @Override
  public @NotNull String getLadder() {
    return this.ladder;
  }
}
