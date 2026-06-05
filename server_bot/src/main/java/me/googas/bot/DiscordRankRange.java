package me.googas.bot;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.api.Range;

/** A rank range is a range for certain elo rank */
public class DiscordRankRange implements Range {

  @NonNull @Getter @Setter private String ladder;
  @NonNull @Getter @Setter private String name;
  @Getter @Setter private int min;
  @Getter @Setter private int max;
  @Getter @Setter private long roleId;

  /**
   * Create the rank range
   *
   * @param ladder the ladder where this range applies
   * @param min the minimum value of the range
   * @param max the maximum value of the range
   */
  public DiscordRankRange(
      @NonNull String ladder, @NonNull String name, int min, int max, long roleId) {
    this.ladder = ladder;
    this.name = name;
    this.min = min;
    this.max = max;
    this.roleId = roleId;
  }

  public DiscordRankRange() {
    this("", "", 0, 0, 0);
  }
}
