package me.googas.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import lombok.NonNull;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.utility.SortedStats;

/** This object represents an entity that can have stats */
public interface Stateable {

  @NonNull
  default Map<String, Float> getStats(@NonNull String context) {
    Map<String, Float> map = this.getStats().get(context);
    if (map == null) return new HashMap<>();
    return map;
  }

  @NonNull
  default Map<String, Float> getStatsOrCreate(@NonNull String context) {
    return this.getStats().computeIfAbsent(context, k -> new HashMap<>());
  }

  /**
   * Increase the elo in certain ladder
   *
   * @param context
   * @param ladder the ladder to increase the elo
   * @param amount the amount of elo to increase
   */
  default void increaseElo(@NonNull String context, @NonNull Ladder ladder, float amount) {
    Map<String, Float> map = this.getStatsOrCreate(context);
    map.put(
        ladder.getName() + "-elo",
        map.getOrDefault(ladder.getName() + "-elo", (float) ladder.baseValue()) + amount);
  }

  /**
   * Decreases the elo in certain ladder
   *
   * @param context
   * @param ladder the ladder to decrease elo
   * @param amount the amount to decrease
   */
  default void decreaseElo(@NonNull String context, @NonNull Ladder ladder, float amount) {
    Map<String, Float> map = this.getStatsOrCreate(context);
    float result = map.getOrDefault(ladder.getName() + "-elo", (float) ladder.baseValue()) - amount;
    result = result < 0 ? 0 : result;
    map.put(ladder.getName() + "-elo", result);
  }

  /**
   * Increases the stat for the given key
   *
   * @param context
   * @param key the key of the stat
   * @param amount the amount to increase the stat
   */
  default void increaseStat(@NonNull String context, @NonNull String key, float amount) {
    Map<String, Float> map = this.getStatsOrCreate(context);
    map.put(key, map.getOrDefault(key, 0f) + amount);
  }

  /**
   * Get the amount of time won in this ladder
   *
   * @param context
   * @param ladder the ladder to getId the wins
   * @return the amount of time won in the ladder
   */
  default float getWins(@NonNull String context, @NonNull Ladder ladder) {
    return this.getStats(context).getOrDefault(ladder.getName() + "-wins", 0f);
  }

  /**
   * The amount of times lost in a ladder
   *
   * @param context
   * @param ladder the ladder
   * @return the amount of times lost
   */
  default float getLoses(@NonNull String context, @NonNull Ladder ladder) {
    return this.getStats(context).getOrDefault(ladder.getName() + "-loses", 0f);
  }

  /**
   * Get an stat
   *
   * @param context
   * @param stat the key of the stat
   * @return the stat or 0 if none
   */
  default float getStat(@NonNull String context, @NonNull String stat) {
    return this.getStats(context).getOrDefault(stat, 0f);
  }

  /**
   * Get the global elo in certain guild for this data
   *
   * @param ladders the ladders to calculate the global elo with
   * @return the global elo inside the guild
   */
  default float getGlobalElo(@NonNull Collection<Ladder> ladders) {
    float sum = 0;
    float total = ladders.size();
    for (Ladder ladder : ladders) {
      // TODO check in every context
      sum += this.getElo("none", ladder);
    }
    return sum / total;
  }

  /**
   * Get the elo for certain ranked ladder
   *
   * @param context
   * @param ladder the ladder to getId the elo from
   * @return the elo for certain ranked ladder
   */
  default float getElo(@NonNull String context, @NonNull Ladder ladder) {
    return this.getStats(context)
        .getOrDefault(ladder.getName() + "-elo", (float) ladder.baseValue());
  }

  /**
   * Get the stats organized
   *
   * @param ladders the ladders to calculate the global elo
   * @return the organized stats
   */
  default SortedStats getOrganized(Collection<Ladder> ladders) {
    Map<String, Map<String, Float>> map = new TreeMap<>(this.getStats());
    map.put("global", this.getGlobalStats(ladders, map));
    return new SortedStats(map);
  }

  /**
   * Get the global stats
   *
   * @param ladders the ladders to getId the global elo
   * @param map the map containing all the other stats
   * @return the global stats
   */
  @NonNull
  default Map<String, Float> getGlobalStats(
      Collection<Ladder> ladders, Map<String, Map<String, Float>> map) {
    Map<String, Float> global = new TreeMap<>();
    map.forEach(
        (context, contextMap) ->
            contextMap.forEach(
                (stat, value) -> {
                  if (!stat.startsWith("elo")) {
                    global.put(stat, global.getOrDefault(stat, 0f) + value);
                  }
                }));
    if (ladders != null) {
      global.put("elo", this.getGlobalElo(ladders));
    }
    return global;
  }

  default void setElo(@NonNull String context, Ladder ladder, float elo) {
    this.getStatsOrCreate(context).put(ladder.getName() + "-elo", elo);
  }

  /**
   * Resets the wins and loses on the given ladders
   *
   * @param ladders the ladders to reset the win and loses
   */
  default void resetWinsAndLoses(@NonNull Collection<Ladder> ladders) {
    for (Ladder ladder : ladders) {
      this.resetWinsAndLoses("none", ladder);
    }
  }

  /**
   * Reset the stats of this entity
   *
   * @param clearElo whether to clear elo too
   */
  default void reset(boolean clearElo) {
    if (this.getStats().isEmpty()) return;
    Set<String> keysToRemove = new HashSet<>();
    this.getStats()
        .forEach(
            (key, value) -> {
              if (key.endsWith("elo") && clearElo) {
                keysToRemove.add(key);
              } else if (!key.endsWith("elo")) {
                keysToRemove.add(key);
              }
            });
    if (!keysToRemove.isEmpty()) {
      for (String key : keysToRemove) {
        this.getStats().remove(key);
      }
    }
  }

  /**
   * Resets the wins and loses on the given ladder
   *
   * @param context
   * @param ladder the ladder to reset the win and loses
   */
  default void resetWinsAndLoses(@NonNull String context, @NonNull Ladder ladder) {
    this.getStatsOrCreate(context).put(ladder.getName() + "-wins", 0f);
    this.getStatsOrCreate(context).put(ladder.getName() + "-loses", 0f);
    this.getStatsOrCreate(context).put(ladder.getName() + "-played", 0f);
  }

  /**
   * Get the stats of the entity
   *
   * @return the map of the stats
   */
  @NonNull
  Map<String, Map<String, Float>> getStats();
}
