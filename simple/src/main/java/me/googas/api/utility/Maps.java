package me.googas.api.utility;

import java.util.Map;
import lombok.NonNull;
import me.googas.starbox.builders.MapBuilder;

public class Maps {

  public static @NonNull <K, V> MapBuilder<K, V> builder() {
    return new MapBuilder<>();
  }

  public static @NonNull <K, V> MapBuilder<K, V> builder(@NonNull K key, V value) {
    return new MapBuilder<K, V>().put(key, value);
  }

  public static @NonNull <K> MapBuilder<K, Object> objects(@NonNull K key, Object value) {
    return new MapBuilder<K, Object>().put(key, value);
  }

  public static @NonNull <K, V> Map<K, V> singleton(@NonNull K key, V value) {
    return builder(key, value).build();
  }
}
