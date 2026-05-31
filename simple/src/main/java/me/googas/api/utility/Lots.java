package me.googas.api.utility;

import java.util.*;
import lombok.NonNull;

public class Lots {

  @NonNull
  public static String pretty(@NonNull Collection<?> collection, @NonNull String empty) {
    if (collection.isEmpty()) return empty;
    return collection.stream().map(Object::toString).reduce((a, b) -> a + ", " + b).orElse("none");
  }

  @NonNull
  public static String pretty(@NonNull Collection<?> collection) {
    return pretty(collection, "none");
  }

  public static <E> Set<E> set(@NonNull E... elements) {
    return new HashSet<>(Arrays.asList(elements));
  }

  public static <E> List<E> list(@NonNull E... elements) {
    return Arrays.asList(elements);
  }
}
