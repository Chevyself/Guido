package me.googas.bot.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.NonNull;
import me.googas.api.utility.Lots;
import me.googas.bot.DiscordRankRange;

/** Static utilities for ranks */
public class Ranks {

  @NonNull
  public static String getRankToken(@NonNull DiscordRankRange range) {
    return "%rank." + range.getName() + "%";
  }

  @NonNull
  public static String getRanksToken(@NonNull Collection<DiscordRankRange> ranges) {
    return Lots.pretty(Ranks.getListRanksToken(ranges));
  }

  @NonNull
  public static List<String> getListRanksToken(@NonNull Collection<DiscordRankRange> ranges) {
    List<String> tokens = new ArrayList<>();
    for (DiscordRankRange range : ranges) {
      tokens.add(Ranks.getRankToken(range));
    }
    return tokens;
  }
}
