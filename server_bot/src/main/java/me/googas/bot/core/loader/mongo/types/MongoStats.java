package me.googas.bot.core.loader.mongo.types;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.ladder.GlobalLadder;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.stats.Stats;
import me.googas.api.utility.ImmutableMap;
import me.googas.bot.core.loader.mongo.MongoStatsLoader;
import me.googas.bot.core.loader.types.GenericStatsId;
import me.googas.starbox.logging.LoggerFactory;
import org.bson.codecs.pojo.annotations.BsonId;

public class MongoStats implements Stats {

  private static final Logger logger = LoggerFactory.getLogger(MongoStats.class);

  @NonNull @BsonId @Getter private final GenericStatsId id;
  @NonNull private final Map<String, Double> values = new HashMap<>();
  private transient MongoStatsLoader loader;

  public MongoStats(@NonNull GenericStatsId id) {
    this.id = id;
  }

  @NonNull
  public MongoStats setLoader(@NonNull MongoStatsLoader loader) {
    this.loader = loader;
    return this;
  }

  public double getElo(@NonNull Ladder ladder, @NonNull Collection<Ladder> ladders) {
    if (!(ladder instanceof GlobalLadder)) return this.getElo(ladder);
    double sum = 0;
    int total = ladders.size();
    for (Ladder other : ladders) {
      sum += this.getElo(other);
    }
    return sum / total;
  }

  @Override
  public double getElo(@NonNull Ladder ladder) {
    return values.getOrDefault(
        ladder.getName() + Stats.LADDER_ELO_SUFFIX, (double) ladder.baseValue());
  }

  @Override
  public double getWins(@NonNull Ladder ladder) {
    return values.getOrDefault(ladder.getName() + Stats.LADDER_WINS_SUFFIX, 0d);
  }

  @Override
  public double getLoses(@NonNull Ladder ladder) {
    return values.getOrDefault(ladder.getName() + Stats.LADDER_LOSES_SUFFIX, 0d);
  }

  @Override
  public double getStat(@NonNull String key) {
    return values.getOrDefault(key, 0d);
  }

  @Override
  public @NonNull ImmutableMap<String, Double> getMap() {
    return new ImmutableMap<>(values);
  }

  private void changeStat(@NonNull String key, double value) {
    if (this.loader == null) {
      logger.log(
          Level.WARNING,
          String.format(
              "Failed to change stat from %s, trying with key %s and value %f, but loader is not set",
              this, key, value),
          new IllegalStateException());
      return;
    }
    if (this.loader.changeStat(this, key, value)) {
      this.values.put(key, value);
    }
  }

  @Override
  public void increaseElo(@NonNull Ladder ladder, float winnersDifference) {}

  @Override
  public void increaseWins(@NonNull Ladder ladder, float value) {}

  @Override
  public void decreaseElo(@NonNull Ladder ladder, float losersDifference) {}

  @Override
  public void increaseLoses(@NonNull Ladder ladder, int value) {}

  @Override
  public void increasePlayed(@NonNull Ladder ladder, int value) {}
}
