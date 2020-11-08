package me.googas.api.user;

import me.googas.commons.cache.Catchable;
import org.jetbrains.annotations.NotNull;

/** The data of a discord user not required to be in a guild */
public interface UserData extends Catchable {

  /**
   * Get the unique id of the user
   *
   * @return the unique id of the user
   */
  @NotNull
  String getId();
}
