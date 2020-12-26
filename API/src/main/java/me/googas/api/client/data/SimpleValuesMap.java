package me.googas.api.client.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.NonNull;
import me.googas.api.ValuesMap;
import me.googas.commons.builder.ToStringBuilder;
import me.googas.commons.maps.Maps;

public class SimpleValuesMap implements ValuesMap {

  @NonNull private final Map<String, Object> map;

  /**
   * Create the values map
   *
   * @param map the initial map
   */
  public SimpleValuesMap(@NonNull Map<String, Object> map) {
    this.map = map;
  }

  /**
   * Create the values map
   *
   * @param key the first key of the map
   * @param value the value of the key to put in the map first
   */
  public SimpleValuesMap(@NonNull String key, @NonNull Object value) {
    this(Maps.singleton(key, value));
  }

  /** Create the values map */
  public SimpleValuesMap() {
    this(new HashMap<>());
  }

  @Override
  public @NonNull Map<String, Object> getMap() {
    return this.map;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("map", this.map).build();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || this.getClass() != object.getClass()) return false;
    SimpleValuesMap that = (SimpleValuesMap) object;
    return this.map.equals(that.map);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.map);
  }
}
