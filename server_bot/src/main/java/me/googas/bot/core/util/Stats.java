package me.googas.bot.core.util;

import java.util.Collection;
import lombok.NonNull;
import me.googas.api.Stateable;
import me.googas.api.matches.ladder.GlobalLadder;
import me.googas.api.matches.ladder.Ladder;

public class Stats {

  public static double getElo(
      @NonNull Stateable stateable, @NonNull Ladder ladder, @NonNull Collection<Ladder> ladders) {
    if (ladder instanceof GlobalLadder) return stateable.getGlobalElo(ladders);
    return stateable.getElo("none", ladder);
  }
}
