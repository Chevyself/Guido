package me.googas.api.utility;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import lombok.NonNull;

public final class ImmutableMap<K, V> {

  @NonNull private final Map<K, V> map;

  public ImmutableMap(@NonNull Map<K, V> map) {
    this.map = new HashMap<>(map);
  }

  public void forEach(@NonNull BiConsumer<? super K, ? super V> action) {
    this.map.forEach(action);
  }

  public boolean isEmpty() {
    return map.isEmpty();
  }
}
