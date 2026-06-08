package dev.xevy.guido.mongo.types;

import dev.xevy.guido.mongo.MongoStatsLoader;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import me.googas.api.matches.ladder.GlobalLadder;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.stats.Stats;
import me.googas.api.utility.ImmutableCollection;
import me.googas.api.utility.ImmutableMap;
import org.bson.codecs.pojo.annotations.BsonId;

public class MongoStats implements Stats {

  @NonNull private final MongoStatsLoader loader;
  @NonNull private MongoStats.Document document;

  public MongoStats(@NonNull MongoStatsLoader loader, @NonNull MongoStats.Document document) {
    this.document = document;
    this.loader = loader;
  }

  private double getValue(@NonNull String key, double def) {
    return this.document.values.getOrDefault(key, def);
  }

  private boolean increaseValue(@NonNull String key, double value, double base) {
    return this.loader
        .increase(this, key, value, base)
        .map(
            doc -> {
              this.document = doc;
              return true;
            })
        .orElse(false);
  }

  @Override
  public boolean increaseValue(@NonNull String key, double value) {
    return this.loader
        .increase(this, key, value)
        .map(
            doc -> {
              this.document = doc;
              return true;
            })
        .orElse(false);
  }

  @Override
  public @NonNull MongoStatsId getId() {
    return new MongoStatsId(document.id);
  }

  @Override
  public double getElo(
      @NonNull Ladder ladder, @NonNull ImmutableCollection<? extends Ladder> ladders) {
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
    return this.getValue(ladder.getName() + Stats.LADDER_ELO_SUFFIX, ladder.baseValue());
  }

  @Override
  public double getWins(@NonNull Ladder ladder) {
    return this.getValue(ladder.getName() + Stats.LADDER_WINS_SUFFIX, 0d);
  }

  @Override
  public double getLoses(@NonNull Ladder ladder) {
    return this.getValue(ladder.getName() + Stats.LADDER_LOSES_SUFFIX, 0d);
  }

  @Override
  public double getStat(@NonNull String key) {
    return this.getValue(key, 0d);
  }

  @Override
  public @NonNull ImmutableMap<String, Double> getMap() {
    return new ImmutableMap<>(document.values);
  }

  @Override
  public void increaseElo(@NonNull Ladder ladder, float winnersDifference) {
    this.increaseValue(
        ladder.getName() + Stats.LADDER_ELO_SUFFIX, winnersDifference, ladder.baseValue());
  }

  @Override
  public void increaseWins(@NonNull Ladder ladder, float value) {
    this.increaseValue(ladder.getName() + Stats.LADDER_WINS_SUFFIX, value);
  }

  @Override
  public void decreaseElo(@NonNull Ladder ladder, float losersDifference) {
    this.increaseValue(
        ladder.getName() + Stats.LADDER_ELO_SUFFIX, losersDifference, ladder.baseValue());
  }

  @Override
  public void increaseLoses(@NonNull Ladder ladder, int value) {
    this.increaseValue(ladder.getName() + Stats.LADDER_LOSES_SUFFIX, value);
  }

  @Override
  public void increasePlayed(@NonNull Ladder ladder, int value) {
    this.increaseValue(ladder.getName() + Stats.LADDER_PLAYED_SUFFIX, value);
  }

  public boolean increaseAll(@NonNull Map<String, Double> stats) {
    return this.loader
        .increaseAll(this, stats)
        .map(
            doc -> {
              this.document = doc;
              return true;
            })
        .orElse(false);
  }

  public static class Document {
    @NonNull @BsonId public MongoStatsId.Document id = new MongoStatsId.Document();
    @NonNull public Map<String, Double> values = new HashMap<>();
  }
}
