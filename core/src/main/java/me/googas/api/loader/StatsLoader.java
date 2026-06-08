package me.googas.api.loader;

import java.util.Map;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.stats.LeaderboardEntry;
import me.googas.api.stats.Stats;

public interface StatsLoader extends DataLoader {
  long maxPageLeaderboard(@NonNull String context, @NonNull Ladder ladder, int limit);

  long maxPageLeaderboard(@NonNull String context, @NonNull String key, int limit);

  @NonNull
  default Map<Integer, ? extends LeaderboardEntry> getLeaderboard(
      @NonNull String context, @NonNull Ladder ladder, int page, int limit) {
    return this.getLeaderboard(context, ladder.getName() + Stats.LADDER_ELO_SUFFIX, page, limit);
  }

  @NonNull
  Map<Integer, ? extends LeaderboardEntry> getLeaderboard(
      @NonNull String context, @NonNull String key, int page, int limit);

  @NonNull
  default Stats getForMinecraftLink(
      @NonNull MinecraftLinkable minecraftLink, @NonNull String context) {
    return this.getForMinecraftLink(minecraftLink.getId(), context);
  }

  @NonNull
  Stats getForMinecraftLink(@NonNull UUID id, @NonNull String context);

  void saveForMinecraftLink(
      @NonNull UUID id, @NonNull String context, @NonNull Map<String, Double> stats);
}
