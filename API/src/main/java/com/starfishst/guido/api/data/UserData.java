package com.starfishst.guido.api.data;

/** The data of a discord user not required to be in a guild */
public interface UserData {

  /**
   * Get the unique id of the discord user
   *
   * @return the unique id of the discord user
   */
  long getId();
}
