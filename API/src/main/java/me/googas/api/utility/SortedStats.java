package me.googas.api.utility;

import java.util.Map;
import java.util.Objects;
import me.googas.commons.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;

/** This object is used to get the stats from a {@link Stateable} is a way more organized way */
public class SortedStats {

  /** A map for each context and the stats of the context */
  @NotNull private final Map<String, Map<String, Float>> map;

  /**
   * Create the sorted stats
   *
   * @param map the map of stats-context
   */
  public SortedStats(@NotNull Map<String, Map<String, Float>> map) {
    this.map = map;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || this.getClass() != object.getClass()) return false;
    SortedStats that = (SortedStats) object;
    return this.map.equals(that.map);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.map);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("statsContext", this.map).build();
  }
}
