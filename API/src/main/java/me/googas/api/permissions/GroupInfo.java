package me.googas.api.permissions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Represents the information to get a {@link Group} */
public interface GroupInfo {

  /**
   * The unique way to identify the group
   *
   * @return the id of the group
   */
  @NotNull
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
  @NotNull
  String getName();

  /**
   * Get the real group using the info of this object
   *
   * @return the group if it is found null otherwise
   */
  @Nullable
  Group getGroup();
}
