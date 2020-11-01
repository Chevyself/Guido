package me.googas.bot.core.types.maps;

import java.util.LinkedHashMap;
import java.util.Map;
import me.googas.api.utility.ValuesMap;
import me.googas.commons.maps.Maps;
import org.jetbrains.annotations.NotNull;

/** A guido linked values map uses {@link java.util.LinkedHashMap} instead of a hash map */
public class GuidoLinkedValuesMap extends GuidoValuesMap {

  /**
   * Create the linked values map
   *
   * @param map to create the map from
   */
  public GuidoLinkedValuesMap(@NotNull Map<String, Object> map) {
    super(new LinkedHashMap<>(map));
  }

  /**
   * Create the linked values map from an starting key and value
   *
   * @param key the key to start the map
   * @param value the value to start the map
   */
  public GuidoLinkedValuesMap(@NotNull String key, @NotNull Object value) {
    super(new LinkedHashMap<>(Maps.singleton(key, value)));
  }

  /** Create a guido linked values */
  public GuidoLinkedValuesMap() {
    super(new LinkedHashMap<>());
  }

  @Override
  public GuidoLinkedValuesMap addValue(@NotNull String name, @NotNull Object value) {
    return (GuidoLinkedValuesMap) super.addValue(name, value);
  }

  @Override
  public GuidoLinkedValuesMap addValues(@NotNull Map<String, Object> map) {
    return (GuidoLinkedValuesMap) super.addValues(map);
  }

  @Override
  public GuidoLinkedValuesMap addValues(@NotNull ValuesMap map) {
    return (GuidoLinkedValuesMap) super.addValues(map);
  }
}
