package me.googas.bot.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.NonNull;
import me.googas.api.ranks.RankRange;
import me.googas.api.utility.Lots;

/** Static utilities for ranks */
public class Ranks {

  @NonNull
  public static String getRankToken(@NonNull RankRange range) {
    return "%rank." + range.getString(null, "name", "no-name").replace(" ", "-") + "%";
  }

  @NonNull
  public static String getRanksToken(@NonNull Collection<RankRange> ranges) {
    return Lots.pretty(Ranks.getListRanksToken(ranges));
  }

  @NonNull
  public static List<String> getListRanksToken(@NonNull Collection<RankRange> ranges) {
    List<String> tokens = new ArrayList<>();
    for (RankRange range : ranges) {
      tokens.add(Ranks.getRankToken(range));
    }
    return tokens;
  }
}
