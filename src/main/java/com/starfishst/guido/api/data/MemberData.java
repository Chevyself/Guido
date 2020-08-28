package com.starfishst.guido.api.data;

import java.util.HashMap;
import org.jetbrains.annotations.NotNull;

/**
 * The data for a member. This class represents all the data for certain discord user that is inside
 * a guild. This is called a member
 */
public interface MemberData extends Permissible {

  /**
   * Get the unique id of the discord user
   *
   * @return the unique id of the discord user
   */
  long getId();

  /**
   * Get the unique id where this entity is a member
   *
   * @return the unique id of the guild
   */
  long getGuildId();

  /**
   * Get the stats of the member inside the server
   *
   * @return the stats of the member
   */
  @NotNull
  HashMap<String, Double> getStats();
}
