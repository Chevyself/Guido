package me.googas.api.permissions;

import java.util.Collection;
import lombok.NonNull;
import me.googas.api.utility.ValuesMap;
import me.googas.commons.cache.Catchable;

/** This class represents a group which can be used to have multiple permissions in one */
public interface Group extends Permissible, Catchable {

  /**
   * Set the weight of the group
   *
   * @param weight the new weight of the group
   */
  void setWeight(int weight);

  /**
   * Set the name of the group
   *
   * @param name the new name of the group
   */
  void setName(@NonNull String name);

  /**
   * The unique way to identify the group
   *
   * @return the id of the group
   */
  @NonNull
  String getId();

  /**
   * Get the weight of the group
   *
   * @return the weight
   */
  int getWeight();

  /**
   * Get the group using its name. This is a more readable way to get it instead of an id
   *
   * @return the name of the group
   */
  @NonNull
  String getName();

  /**
   * Get the preferences of the group
   *
   * @return the preferences of the group
   */
  @NonNull
  ValuesMap getPreferences();

  /**
   * Get the id of the groups that are parent of this one. It can be given sorted with the weight of
   * each group
   *
   * @return the collection of ids
   */
  @NonNull
  Collection<String> getParents();
}
