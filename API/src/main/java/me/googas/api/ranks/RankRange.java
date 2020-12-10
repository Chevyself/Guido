package me.googas.api.ranks;

import lombok.NonNull;
import me.googas.api.utility.Range;

/** A rank range is a range for certain elo rank */
public interface RankRange extends Range {

  /**
   * Get the ladder where this range applies
   *
   * @return the ladder where this range applies
   */
  @NonNull
  String getLadder();
}
