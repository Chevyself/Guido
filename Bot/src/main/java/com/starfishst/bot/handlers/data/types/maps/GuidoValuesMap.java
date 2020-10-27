package com.starfishst.bot.handlers.data.types.maps;

import com.starfishst.guido.api.data.ValuesMap;
import java.util.HashMap;
import java.util.Map;
import me.googas.commons.maps.Maps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** An implementation of {@link ValuesMap} */
public class GuidoValuesMap implements ValuesMap {

  /** The map of values */
  @NotNull private final Map<String, Object> map;

  /**
   * Create the values map
   *
   * @param map the map of values
   */
  public GuidoValuesMap(@NotNull Map<String, Object> map) {
    this.map = map;
  }

  /**
   * Create the values map
   *
   * @param key the initial key
   * @param value the initial value
   */
  public GuidoValuesMap(@NotNull String key, @NotNull Object value) {
    this.map = Maps.singleton(key, value);
  }

  /** Create the values map */
  public GuidoValuesMap() {
    this(new HashMap<>());
  }

  @Override
  public @NotNull Map<String, Object> getMap() {
    return this.map;
  }

  /**
   * Check whether this values map equals to another map
   *
   * @param that the map to compare
   * @return true if the maps are the same
   */
  private boolean equalsMap(@NotNull Map<?, ?> that) {
    if (this.map.isEmpty() || that.isEmpty()) {
      return false;
    } else if (this.map.size() != that.size()) {
      return false;
    } else {
      for (String key : this.map.keySet()) {
        if (!that.containsKey(key)
            || that.containsKey(key) && !that.get(key).equals(this.map.get(key))) {
          return false;
        }
      }
      return true;
    }
  }

  @Override
  public boolean equals(@Nullable Object object) {
    if (this == object) return true;
    if (object instanceof Map) {
      Map<?, ?> that = (Map<?, ?>) object;
      return this.equalsMap(that);
    } else if (object instanceof ValuesMap) {
      return this.equalsMap(((ValuesMap) object).getMap());
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return this.map.hashCode();
  }

  @Override
  public GuidoValuesMap addValue(@NotNull String name, @NotNull Object value) {
    return (GuidoValuesMap) ValuesMap.super.addValue(name, value);
  }

  @Override
  public GuidoValuesMap addValues(@NotNull Map<String, Object> map) {
    return (GuidoValuesMap) ValuesMap.super.addValues(map);
  }

  @Override
  public GuidoValuesMap addValues(@NotNull ValuesMap map) {
    return (GuidoValuesMap) ValuesMap.super.addValues(map);
  }

  @Override
  public String toString() {
    return "GuidoValuesMap{" + "map=" + this.map + '}';
  }
}
