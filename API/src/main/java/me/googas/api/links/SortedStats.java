package me.googas.api.links;

import java.util.Map;
import org.jetbrains.annotations.NotNull;

/** This object is used to get the stats from a {@link Stateable} is a way more organized way */
public class SortedStats {

  /** A map for each context and the stats of the context */
  @NotNull private final Map<String, Map<String, Float>> statsContext;

  /**
   * Create the sorted stats
   *
   * @param map the map of stats-context
   */
  public SortedStats(@NotNull Map<String, Map<String, Float>> map) {
    this.statsContext = map;
  }

  @Override
  public String toString() {
    return "OrganizedStats{" + "statsContext=" + this.statsContext + '}';
  }
}
