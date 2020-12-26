package me.googas.api.ranks;

import lombok.NonNull;
import me.googas.api.Range;
import me.googas.api.ValuesMap;

/** A rank range is a range for certain elo rank */
public interface RankRange extends Range {

  /**
   * Get the ladder where this range applies
   *
   * @return the ladder where this range applies
   */
  @NonNull
  String getLadder();

  /**
   * Get the preferences of the rank range
   *
   * @return the preferences of the range of a map
   */
  @NonNull
  ValuesMap getPreferences();
}
