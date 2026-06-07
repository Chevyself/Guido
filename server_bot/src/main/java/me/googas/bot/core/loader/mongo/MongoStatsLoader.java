package me.googas.bot.core.loader.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.loader.StatsLoader;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.stats.LeaderboardEntry;
import me.googas.bot.core.loader.mongo.types.MongoStats;
import me.googas.bot.core.loader.types.GenericStatsId;

public class MongoStatsLoader extends SimpleMongoLoader implements StatsLoader {

  @NonNull @Getter private final MongoLoader loader;
  @NonNull private final MongoCollection<MongoStats> collection;

  public MongoStatsLoader(
      @NonNull MongoLoader loader, @NonNull MongoCollection<MongoStats> collection) {
    this.loader = loader;
    this.collection = collection;
  }

  @Override
  public long maxPageLeaderboard(@NonNull String context, @NonNull Ladder ladder, int limit) {
    if (limit == 0) return 0;
    return this.collection.countDocuments(Filters.eq("_id.context", context));
  }

  @Override
  public long maxPageLeaderboard(@NonNull String context, @NonNull String key, int limit) {
    if (limit == 0) return 0;
    return this.collection.countDocuments(Filters.eq("_id.context", context));
  }

  @Override
  public @NonNull Map<Integer, LeaderboardEntry> getLeaderboard(
      @NonNull String context, @NonNull Ladder ladder, int page, int limit) {
    // TODO
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  public @NonNull Map<Integer, LeaderboardEntry> getLeaderboard(
      @NonNull String context, @NonNull String key, int page, int limit) {
    // TODO
    throw new UnsupportedOperationException("TODO");
  }

  @NonNull
  private Optional<MongoStats> getForMinecraftLinkById(
      @NonNull UUID minecraftLinkId, @NonNull String context) {
    MongoStats match =
        this.collection
            .find(
                Filters.and(
                    Filters.eq("_id.linkableId", minecraftLinkId),
                    Filters.eq("_id.context", context)))
            .first();
    if (match != null) match.setLoader(this);
    return Optional.ofNullable(match);
  }

  @Override
  public @NonNull MongoStats getForMinecraftLink(@NonNull UUID id, @NonNull String context) {
    return this.getForMinecraftLinkById(id, context)
        .orElseGet(() -> new MongoStats(new GenericStatsId(context, id)).setLoader(this));
  }

  public boolean changeStat(@NonNull MongoStats mongoStats, @NonNull String key, double value) {
    return this.collection
            .updateOne(Filters.eq("_id", mongoStats.getId()), Updates.inc(key, value))
            .getModifiedCount()
        > 0;
  }
}
