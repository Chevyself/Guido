package me.googas.bot.core;

import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import me.googas.api.ValuesMap;
import me.googas.api.utility.Maps;

/** An implementation of {@link ValuesMap} */
public class GuidoValuesMap implements ValuesMap {

  /** The map of values */
  @NonNull private final Map<String, Object> map;

  /**
   * Create the values map
   *
   * @param map the map of values
   */
  public GuidoValuesMap(@NonNull Map<String, Object> map) {
    this.map = map;
  }

  /**
   * Create the values map
   *
   * @param key the initial key
   * @param value the initial value
   */
  public GuidoValuesMap(@NonNull String key, @NonNull Object value) {
    this.map = Maps.singleton(key, value);
  }

  /** Create the values map */
  public GuidoValuesMap() {
    this(new HashMap<>());
  }

  public GuidoValuesMap(@NonNull ValuesMap preferences) {
    this(preferences.getMap());
  }

  @Override
  public @NonNull Map<String, Object> getMap() {
    return this.map;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object instanceof Map) {
      return this.isSimilar((Map<?, ?>) object);
    } else if (object instanceof ValuesMap) {
      return this.isSimilar(((ValuesMap) object).getMap());
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return this.map.hashCode();
  }

  @NonNull
  @Override
  public GuidoValuesMap put(@NonNull String name, @NonNull Object value) {
    return (GuidoValuesMap) ValuesMap.super.put(name, value);
  }

  @Override
  public GuidoValuesMap put(@NonNull Map<String, Object> map) {
    return (GuidoValuesMap) ValuesMap.super.put(map);
  }

  @Override
  public GuidoValuesMap put(@NonNull ValuesMap map) {
    return (GuidoValuesMap) ValuesMap.super.put(map);
  }

  @Override
  public String toString() {
    return "GuidoValuesMap{" + "map=" + this.map + '}';
  }
}
