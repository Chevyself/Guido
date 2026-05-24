package me.googas.api.utility;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder<K, V> {

  private final Map<K, V> map;

  public MapBuilder(Map<K, V> map) {
    this.map = map;
  }

  public MapBuilder() {
    this.map = new HashMap<>();
  }

  public MapBuilder<K, V> put(K key, V value) {
    this.map.put(key, value);
    return this;
  }

  public java.util.Map<K, V> build() {
    return this.map;
  }
}
