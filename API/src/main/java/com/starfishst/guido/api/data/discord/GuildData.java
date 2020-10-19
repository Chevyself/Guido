package com.starfishst.guido.api.data.discord;

import com.starfishst.guido.api.data.RankRange;
import com.starfishst.guido.api.data.matches.Ladder;
import java.util.Collection;
import java.util.Map;
import me.googas.commons.cache.ICatchable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** This object represents the data for a guild */
public interface GuildData extends ICatchable {

  /**
   * Get the unique id of the guild. This is an object in discord that must have its unique id
   *
   * @return the unique id of the guild
   */
  long getId();

  /**
   * Get the multipliers for stuff inside the guild.
   *
   * <p>Those multipliers can be used in a lot of stuff mainly used to set the elo in ladders
   *
   * @return the map of multipliers
   */
  Map<String, Integer> getMultipliers();

  /**
   * Get a ladder using its name
   *
   * @param name the name of the ladder
   * @return the ladder if found else null
   */
  @Nullable
  default Ladder getLadder(@NotNull String name) {
    for (Ladder ladder : this.getLadders()) {
      if (ladder.getName().equalsIgnoreCase(name)) {
        return ladder;
      }
    }
    return null;
  }

  /**
   * This map contains the ids of roles and it's respective rank range. This is used to give roles
   * in certain ladders when someone reaches certain elo
   *
   * @return the ranges
   */
  Map<Long, ? extends RankRange> getRanges();

  /**
   * Get the ladders of the guild and the ladder base value
   *
   * @return the map of the ladders and its initial base value
   */
  Collection<? extends Ladder> getLadders();
}
