package dev.xevy.guido.mongo.types;

import lombok.Getter;
import lombok.NonNull;
import me.googas.server.RankRange;

public class MongoRankRange implements RankRange {

  @NonNull @Getter private final String ladder;
  @NonNull @Getter private final String name;
  @Getter private final int min;
  @Getter private final int max;
  @Getter private final long roleId;

  /**
   * Create the rank range
   *
   * @param ladder the ladder where this range applies
   * @param min the minimum value of the range
   * @param max the maximum value of the range
   */
  public MongoRankRange(
      @NonNull String ladder, @NonNull String name, int min, int max, long roleId) {
    this.ladder = ladder;
    this.name = name;
    this.min = min;
    this.max = max;
    this.roleId = roleId;
  }

  public MongoRankRange() {
    this("", "", 0, 0, 0);
  }
}
