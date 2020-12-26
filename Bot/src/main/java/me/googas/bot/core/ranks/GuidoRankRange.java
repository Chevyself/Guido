package me.googas.bot.core.ranks;

import lombok.NonNull;
import me.googas.api.ValuesMap;
import me.googas.api.ranks.RankRange;
import me.googas.bot.core.GuidoValuesMap;

/** Rank range implementation for the guido bot */
public class GuidoRankRange implements RankRange {

  @NonNull private final String ladder;
  private final int min;
  private final int max;
  private final GuidoValuesMap preferences;

  /**
   * Create the range
   *  @param ladder the ladder that will use this range
   * @param min the minimum value of the range
   * @param max the maximum value of the range
   * @param preferences the preferences of the rank range
   */
  public GuidoRankRange(@NonNull String ladder, int min, int max, GuidoValuesMap preferences) {
    this.ladder = ladder;
    this.min = min;
    this.max = max;
    this.preferences = preferences;
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoRankRange() {
    this("", -1, -1, new GuidoValuesMap());
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

  @Override
  public @NonNull ValuesMap getPreferences() {
    return this.preferences;
  }
}
