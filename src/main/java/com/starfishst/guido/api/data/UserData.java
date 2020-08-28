package com.starfishst.guido.api.data;

import com.starfishst.guido.api.lang.Localizable;

/** The data of a discord user not required to be in a guild */
public interface UserData extends Localizable, Permissible {

  /**
   * Get the unique id of the discord user
   *
   * @return the unique id of the discord user
   */
  long getId();
}
