package me.googas.bot.core.types;

import lombok.NonNull;
import me.googas.api.ranks.RankRange;

/** Rank range implementation for the guido bot */
public class GuidoRankRange implements RankRange {

  /** The ladder that is using this range */
  @NonNull private final String ladder;

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
  public GuidoRankRange(@NonNull String ladder, int min, int max) {
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
  public @NonNull String getLadder() {
    return this.ladder;
  }
}
