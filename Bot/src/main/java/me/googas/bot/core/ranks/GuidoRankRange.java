package me.googas.bot.core.ranks;

import java.util.Objects;
import lombok.NonNull;
import me.googas.api.ValuesMap;
import me.googas.api.ranks.RankRange;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.commons.builder.ToStringBuilder;

/** Rank range implementation for the guido bot */
public class GuidoRankRange implements RankRange {

  @NonNull private final String ladder;
  private final int min;
  private final int max;
  private final GuidoValuesMap preferences;

  /**
   * Create the range
   *
   * @param ladder the ladder that will use this range
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || this.getClass() != o.getClass()) return false;
    GuidoRankRange that = (GuidoRankRange) o;
    return this.min == that.min && this.max == that.max && this.ladder.equals(that.ladder);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.ladder, this.min, this.max);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("ladder", this.ladder)
        .append("min", this.min)
        .append("max", this.max)
        .append("preferences", this.preferences)
        .build();
  }
}
