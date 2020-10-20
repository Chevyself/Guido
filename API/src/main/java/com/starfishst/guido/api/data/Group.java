package com.starfishst.guido.api.data;

import me.googas.commons.cache.ICatchable;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents a group which can be used to have multiple permissions in one
 *
 * @param <T> the type of permission this supports
 * @param <K> the type of permission stack this can contain
 */
public interface Group<T extends Permission, K extends PermissionStack<T>>
    extends Permissible<T, K>, ICatchable {

  /**
   * The unique way to identify the group
   *
   * @return the id of the group
   */
  @NotNull
  String getId();

  /**
   * Get the preferences of the group
   *
   * @return the preferences
   */
  ValuesMap getPreferences();
}
