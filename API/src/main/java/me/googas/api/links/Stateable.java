package me.googas.api.links;

import java.util.Map;
import me.googas.api.discord.GuildData;
import me.googas.api.matches.Ladder;
import org.jetbrains.annotations.NotNull;

/** This object represents an entity that can have stats */
public interface Stateable {

  /**
   * Increase the elo in certain ladder
   *
   * @param ladder the ladder to increase the elo
   * @param amount the amount of elo to increase
   */
  default void increaseElo(@NotNull Ladder ladder, float amount) {
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
  default void decreaseElo(@NotNull Ladder ladder, float amount) {
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
  default void increaseStat(@NotNull String key, float amount) {
    this.getStats().put(key, this.getStats().getOrDefault(key, 0f) + amount);
  }

  /**
   * Get the amount of time won in this ladder
   *
   * @param ladder the ladder to get the wins
   * @return the amount of time won in the ladder
   */
  default float getWins(@NotNull Ladder ladder) {
    return this.getStats().getOrDefault(ladder.getName() + "-wins", 0f);
  }

  /**
   * The amount of times lost in a ladder
   *
   * @param ladder the ladder
   * @return the amount of times lost
   */
  default float getLoses(@NotNull Ladder ladder) {
    return this.getStats().getOrDefault(ladder.getName() + "-loses", 0f);
  }

  /**
   * Get an stat
   *
   * @param stat the key of the stat
   * @return the stat or 0 if none
   */
  default float getStat(@NotNull String stat) {
    return this.getStats().getOrDefault(stat, 0f);
  }

  /**
   * Get the stats of the entity
   *
   * @return the map of the stats
   */
  @NotNull
  Map<String, Float> getStats();

  /**
   * Get the global elo in certain guild for this data
   *
   * @param data the data to get the elo from
   * @return the global elo inside the guild
   */
  default float getGlobalElo(@NotNull GuildData data) {
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
  default float getElo(@NotNull Ladder ladder) {
    return this.getStats().getOrDefault(ladder.getName() + "-elo", (float) ladder.baseValue());
  }
}
