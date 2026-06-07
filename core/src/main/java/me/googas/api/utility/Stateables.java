package me.googas.api.utility;

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import me.googas.api.Range;

public class Stateables {

  @NonNull
  public static <T extends Range> List<T> getApplying(
      @NonNull Number number, @NonNull ImmutableCollection<T> ranges) {
    List<T> applying = new ArrayList<>();
    if (ranges.isEmpty()) return applying;
    for (T range : ranges) {
      if (range.isBound(number.intValue())) applying.add(range);
    }
    return applying;
  }

  @NonNull
  public static <T extends Range> List<T> getOutside(
      @NonNull Number number, @NonNull ImmutableCollection<T> ranges) {
    List<T> outside = new ArrayList<>();
    if (ranges.isEmpty()) return outside;
    for (T range : ranges) {
      if (!range.isBound(number.intValue())) outside.add(range);
    }
    return outside;
  }
}
