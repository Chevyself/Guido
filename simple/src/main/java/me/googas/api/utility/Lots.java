package me.googas.api.utility;

import java.util.Collection;

public class Lots {

  public static String pretty(Collection<?> collection) {
    if (collection.isEmpty()) return "none";
    return collection.stream().map(Object::toString).reduce((a, b) -> a + ", " + b).orElse("none");
  }
}
