package com.starfishst.guido.api.data;

import me.googas.commons.cache.ICatchable;
import org.jetbrains.annotations.NotNull;

/** The data of a discord user not required to be in a guild */
public interface UserData extends ICatchable {

  /**
   * Get the unique id of the user
   *
   * @return the unique id of the user
   */
  @NotNull
  String getId();
}
