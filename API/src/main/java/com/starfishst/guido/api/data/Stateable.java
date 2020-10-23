package com.starfishst.guido.api.data;

import com.starfishst.guido.api.data.discord.GuildData;
import com.starfishst.guido.api.data.matches.Ladder;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;

/** This object represents an entity that can have stats */
public interface Stateable {

  /**
   * Increase the elo in certain ladder
   *
   * @param ladder the ladder to increase the elo
   * @param amount the amount of elo to increase
   */
  default void increaseElo(@NotNull Ladder ladder, double amount) {
    String key = ladder.getName() + "-elo";
    this.getStats()
        .put(key, this.getStats().getOrDefault(key, (double) ladder.baseValue()) + amount);
  }

  /**
   * Decreases the elo in certain ladder
   *
   * @param ladder the ladder to decrease elo
   * @param amount the amount to decrease
   */
  default void decreaseElo(@NotNull Ladder ladder, double amount) {
    String key = ladder.getName() + "-elo";
    double value = this.getStats().getOrDefault(key, (double) ladder.baseValue()) - amount;
    this.getStats().put(key, value < 0 ? 0 : value);
  }

  /**
   * Get the stats of the entity
   *
   * @return the map of the stats
   */
  @NotNull
  HashMap<String, Double> getStats();

  /**
   * Get the global elo in certain guild for this data
   *
   * @param data the data to get the elo from
   * @return the global elo inside the guild
   */
  default double getGlobalElo(@NotNull GuildData data) {
    double sum = 0;
    double total = 0;
    for (Ladder ladder : data.getLadders()) {
      sum += this.getElo(ladder);
      total++;
    }
    return sum / total;
  }

  /**
   * Get the elo for certain ranked ladder
   *
   * @param ladder the ladder to get the elo from
   * @return the elo for certain ranked ladder
   */
  default Double getElo(@NotNull Ladder ladder) {
    return this.getStats().getOrDefault(ladder.getName() + "-elo", (double) ladder.baseValue());
  }
}
