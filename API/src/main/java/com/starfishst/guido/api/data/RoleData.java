package com.starfishst.guido.api.data;

/**
 * This object represents the data for a role. Roles can be permissible which makes them have their
 * own data
 */
public interface RoleData {

  /**
   * Get the unique id of the role. This is an object in discord that must have its unique id
   *
   * @return the unique id of the role
   */
  long getId();

  /**
   * Get the unique id where this is a role
   *
   * @return the unique id of the guild
   */
  long getGuildId();
}
