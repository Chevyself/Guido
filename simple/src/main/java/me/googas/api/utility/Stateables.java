package me.googas.api.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.NonNull;
import me.googas.api.ranks.RankRange;

public class Stateables {

  @NonNull
  public static List<RankRange> getApplying(
      @NonNull Number number, @NonNull Collection<RankRange> ranges) {
    List<RankRange> applying = new ArrayList<>();
    if (ranges.isEmpty()) return applying;
    for (RankRange range : ranges) {
      if (range.isBound(number.intValue())) applying.add(range);
    }
    return applying;
  }

  @NonNull
  public static List<RankRange> getOutside(
      @NonNull Number number, @NonNull Collection<RankRange> ranges) {
    List<RankRange> outside = new ArrayList<>();
    if (ranges.isEmpty()) return outside;
    for (RankRange range : ranges) {
      if (!range.isBound(number.intValue())) outside.add(range);
    }
    return outside;
  }
}
