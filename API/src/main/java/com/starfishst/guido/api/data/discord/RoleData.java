package com.starfishst.guido.api.data.discord;

import com.starfishst.guido.api.data.Permissible;
import com.starfishst.guido.api.data.Permission;
import com.starfishst.guido.api.data.PermissionStack;
import me.googas.commons.cache.ICatchable;

/**
 * This object represents the data for a role. Roles can be permissible which makes them have their
 * own data
 *
 * @param <T> the type of permission that the role can contain
 * @param <K> the type of permission stack that the role can support
 */
public interface RoleData<T extends Permission, K extends PermissionStack<T>>
    extends Permissible<T, K>, ICatchable {

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
