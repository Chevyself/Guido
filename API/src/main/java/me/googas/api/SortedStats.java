package me.googas.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;
import me.googas.commons.builder.ToStringBuilder;

/** This object is used to get the stats from a {@link Stateable} is a way more organized way */
public class SortedStats {

  /** A map for each context and the stats of the context */
  @NonNull @Getter private final Map<String, Map<String, Float>> map;

  /**
   * Create the sorted stats
   *
   * @param map the map of stats-context
   */
  public SortedStats(@NonNull Map<String, Map<String, Float>> map) {
    this.map = map;
  }

  public SortedStats() {
    this(new HashMap<>());
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
    return new ToStringBuilder(this).append("map", this.map).build();
  }
}
