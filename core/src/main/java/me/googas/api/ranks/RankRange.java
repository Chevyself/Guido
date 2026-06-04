package me.googas.api.ranks;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.api.Informative;
import me.googas.api.Range;

/** A rank range is a range for certain elo rank */
public class RankRange implements Range, Informative {

  @NonNull @Getter private final Map<String, Map<String, Object>> information;
  @NonNull @Getter @Setter private String ladder;
  @Getter @Setter private int min;
  @Getter @Setter private int max;

  /**
   * Create the rank range
   *
   * @param ladder the ladder where this range applies
   * @param information the preferences of the rank range
   * @param min the minimum value of the range
   * @param max the maximum value of the range
   */
  public RankRange(
      @NonNull String ladder,
      @NonNull Map<String, Map<String, Object>> information,
      int min,
      int max) {
    this.ladder = ladder;
    this.information = information;
    this.min = min;
    this.max = max;
  }

  public RankRange() {
    this("", new HashMap<>(), 0, 0);
  }
}
