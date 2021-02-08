package me.googas.api.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.NonNull;

/** Static utilities for enums */
public class Enums {

  public static <T extends Enum<?>> List<String> getNames(Collection<T> enums) {
    List<String> names = new ArrayList<>();
    if (enums.isEmpty()) return names;
    for (T anEnum : enums) {
      names.add(anEnum.name());
    }
    return names;
  }

  public static <T extends Enum<?>> List<String> getNames(T[] enums) {
    List<String> names = new ArrayList<>();
    if (enums.length == 0) return names;
    for (T anEnum : enums) {
      names.add(anEnum.name());
    }
    return names;
  }

  public static <T extends Enum<?>> boolean contains(@NonNull T[] arr, @NonNull T t) {
    for (T t1 : arr) {
      if (t.equals(t1)) return true;
    }
    return false;
  }
}
