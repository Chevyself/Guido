package com.starfishst.guido.api.data.discord;

import me.googas.commons.cache.ICatchable;

/** This object represents the data for a guild */
public interface GuildData extends ICatchable {

  /**
   * Get the unique id of the guild. This is an object in discord that must have its unique id
   *
   * @return the unique id of the guild
   */
  long getId();
}
