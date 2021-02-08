package me.googas.bot.core;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.NonNull;
import me.googas.api.ValuesMap;
import me.googas.commons.maps.Maps;

/** A guido linked values map uses {@link LinkedHashMap} instead of a hash map */
public class GuidoLinkedValuesMap extends GuidoValuesMap {

  /**
   * Create the linked values map
   *
   * @param map to create the map from
   */
  public GuidoLinkedValuesMap(@NonNull Map<String, Object> map) {
    super(new LinkedHashMap<>(map));
  }

  /**
   * Create the linked values map from an starting key and value
   *
   * @param key the key to start the map
   * @param value the value to start the map
   */
  public GuidoLinkedValuesMap(@NonNull String key, @NonNull Object value) {
    super(new LinkedHashMap<>(Maps.singleton(key, value)));
  }

  /** Create a guido linked values */
  public GuidoLinkedValuesMap() {
    super(new LinkedHashMap<>());
  }

  @NonNull
  @Override
  public GuidoLinkedValuesMap put(@NonNull String name, @NonNull Object value) {
    return (GuidoLinkedValuesMap) super.put(name, value);
  }

  @Override
  public GuidoLinkedValuesMap put(@NonNull Map<String, Object> map) {
    return (GuidoLinkedValuesMap) super.put(map);
  }

  @Override
  public GuidoLinkedValuesMap put(@NonNull ValuesMap map) {
    return (GuidoLinkedValuesMap) super.put(map);
  }
}
