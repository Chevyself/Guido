package me.googas.api.utility;

import lombok.NonNull;

public class Maps {

  public static @NonNull <K, V> MapBuilder<K, V> builder() {
    return new MapBuilder<>();
  }

  public static @NonNull <K, V> MapBuilder<K, V> builder(@NonNull K key, V value) {
    return new MapBuilder<K, V>().put(key, value);
  }
}
