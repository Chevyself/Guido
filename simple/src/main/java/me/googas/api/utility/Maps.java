package me.googas.api.utility;

import java.util.Map;
import lombok.NonNull;

public class Maps {

  public static @NonNull <K, V> MapBuilder<K, V> builder() {
    return new MapBuilder<>();
  }

  public static @NonNull <K, V> MapBuilder<K, V> builder(@NonNull K key, V value) {
    return new MapBuilder<K, V>().append(key, value);
  }

  public static @NonNull <K, V> Map<K, V> singleton(@NonNull K key, V value) {
    return builder(key, value).build();
  }
}
