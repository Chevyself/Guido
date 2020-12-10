package me.googas.api.utility;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import lombok.NonNull;
import me.googas.api.discord.GuildData;
import me.googas.api.matches.Ladder;

/** This object represents an entity that can have stats */
public interface Stateable {

  /**
   * Increase the elo in certain ladder
   *
   * @param ladder the ladder to increase the elo
   * @param amount the amount of elo to increase
   */
  default void increaseElo(@NonNull Ladder ladder, float amount) {
    String key = ladder.getName() + "-elo";
    this.getStats()
        .put(key, this.getStats().getOrDefault(key, (float) ladder.baseValue()) + amount);
  }

  /**
   * Decreases the elo in certain ladder
   *
   * @param ladder the ladder to decrease elo
   * @param amount the amount to decrease
   */
  default void decreaseElo(@NonNull Ladder ladder, float amount) {
    String key = ladder.getName() + "-elo";
    float value = this.getStats().getOrDefault(key, (float) ladder.baseValue()) - amount;
    this.getStats().put(key, value < 0 ? 0 : value);
  }

  /**
   * Increases the stat for the given key
   *
   * @param key the key of the stat
   * @param amount the amount to increase the stat
   */
  default void increaseStat(@NonNull String key, float amount) {
    this.getStats().put(key, this.getStats().getOrDefault(key, 0f) + amount);
  }

  /**
   * Get the amount of time won in this ladder
   *
   * @param ladder the ladder to get the wins
   * @return the amount of time won in the ladder
   */
  default float getWins(@NonNull Ladder ladder) {
    return this.getStats().getOrDefault(ladder.getName() + "-wins", 0f);
  }

  /**
   * The amount of times lost in a ladder
   *
   * @param ladder the ladder
   * @return the amount of times lost
   */
  default float getLoses(@NonNull Ladder ladder) {
    return this.getStats().getOrDefault(ladder.getName() + "-loses", 0f);
  }

  /**
   * Get an stat
   *
   * @param stat the key of the stat
   * @return the stat or 0 if none
   */
  default float getStat(@NonNull String stat) {
    return this.getStats().getOrDefault(stat, 0f);
  }

  /**
   * Get the global elo in certain guild for this data
   *
   * @param data the data to get the elo from
   * @return the global elo inside the guild
   */
  default float getGlobalElo(@NonNull GuildData data) {
    float sum = 0;
    float total = data.getLadders().size();
    for (Ladder ladder : data.getLadders()) {
      sum += this.getElo(ladder);
    }
    return sum / total;
  }

  /**
   * Get the elo for certain ranked ladder
   *
   * @param ladder the ladder to get the elo from
   * @return the elo for certain ranked ladder
   */
  default float getElo(@NonNull Ladder ladder) {
    return this.getStats().getOrDefault(ladder.getName() + "-elo", (float) ladder.baseValue());
  }

  /**
   * Get the stats organized
   *
   * @param guild A guild to get the global elo in there
   * @return the organized stats
   */
  default SortedStats getOrganized(GuildData guild) {
    Map<String, Map<String, Float>> map = new TreeMap<>();
    this.getStats()
        .forEach(
            (key, value) -> {
              String context;
              String stat;
              if (key.contains("-")) {
                String[] split = key.split("-");
                context = split[0];
                stat = split[1];
              } else {
                context = "Unknown";
                stat = key;
              }
              Map<String, Float> contextMap =
                  map.computeIfAbsent(context, string -> new TreeMap<>());
              contextMap.put(stat, value);
            });
    map.put("global", this.getGlobalStats(guild, map));
    return new SortedStats(map);
  }

  /**
   * Get the global stats
   *
   * @param guild the guild to get the global stats
   * @param map the map containing all the other stats
   * @return the global stats
   */
  @NonNull
  default Map<String, Float> getGlobalStats(GuildData guild, Map<String, Map<String, Float>> map) {
    Map<String, Float> global = new TreeMap<>();
    map.forEach(
        (context, contextMap) ->
            contextMap.forEach(
                (stat, value) -> {
                  if (!stat.startsWith("elo")) {
                    global.put(stat, global.getOrDefault(stat, 0f) + value);
                  }
                }));
    if (guild != null) {
      global.put("elo", this.getGlobalElo(guild));
    }
    return global;
  }

  /**
   * Get the stats of the entity
   *
   * @return the map of the stats
   */
  @NonNull
  Map<String, Float> getStats();

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
}
