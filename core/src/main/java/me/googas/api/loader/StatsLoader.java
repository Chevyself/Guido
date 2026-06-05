package me.googas.api.loader;

import java.util.Map;
import lombok.NonNull;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.stats.LeaderboardEntry;

public interface StatsLoader extends DataLoader {
  long maxPageLeaderboard(@NonNull String context, @NonNull Ladder ladder, int limit);

  long maxPageLeaderboard(@NonNull String context, @NonNull String key, int limit);

  @NonNull
  Map<Integer, LeaderboardEntry> getLeaderboard(
      @NonNull String context, @NonNull Ladder ladder, int page, int limit);

  @NonNull
  Map<Integer, LeaderboardEntry> getLeaderboard(
      @NonNull String context, @NonNull String key, int page, int limit);
}
