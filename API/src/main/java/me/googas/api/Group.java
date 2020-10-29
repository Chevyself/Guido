package me.googas.api;

import me.googas.commons.cache.ICatchable;
import org.jetbrains.annotations.NotNull;

/** This class represents a group which can be used to have multiple permissions in one */
public interface Group extends Permissible, ICatchable {

  /**
   * The unique way to identify the group
   *
   * @return the id of the group
   */
  @NotNull
  String getId();

  /**
   * Get the group using its name. This is a more readable way to get it instead of an id
   *
   * @return the name of the group
   */
  @NotNull
  String getName();

  /**
   * Get the preferences of the group
   *
   * @return the preferences
   */
  ValuesMap getPreferences();
}
