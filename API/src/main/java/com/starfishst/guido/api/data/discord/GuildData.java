package com.starfishst.guido.api.data.discord;

import com.starfishst.guido.api.data.RankRange;
import me.googas.commons.cache.ICatchable;

import java.util.Map;

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
   * Those multipliers can be used in a lot of stuff mainly used to set the elo in ladders
   *
   * @return the map of multipliers
   */
  Map<String, Integer> getMultipliers();

  /**
   * Get the ladders of the guild and the ladder base value
   *
   * @return the map of the ladders and its initial base value
   */
  Map<String, Integer> getLadders();

  /**
   * This map contains the ids of roles and it's respective rank range. This is used
   * to give roles in certain ladders when someone reaches certain elo
   *
   * @return the ranges
   */
  Map<Long, ? extends RankRange> getRanges();

}
