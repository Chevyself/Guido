package com.starfishst.guido.api.data.links;

import com.starfishst.guido.api.data.Permissible;
import com.starfishst.guido.api.data.Permission;
import com.starfishst.guido.api.data.Stateable;
import com.starfishst.guido.api.data.UserData;
import com.starfishst.guido.api.data.ValuesMap;
import me.googas.commons.cache.ICatchable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This object represents data that has been linked to an user
 *
 * @param <T> the type of permission that the data can contain
 */
public interface LinkedData<T extends Permission> extends Permissible<T>, Stateable, ICatchable {

  /**
   * Get the type of linked data
   *
   * @return the type of linked data
   */
  @NotNull
  LinkedDataType getType();

  /**
   * Set the linked user to this data
   *
   * @param user the new linked user
   */
  void setLinkedUser(@Nullable UserData user);

  /**
   * Get how this linked data is identified
   *
   * @return the identification of the data
   */
  @NotNull
  ValuesMap getIdentification();

  /**
   * Get the preferences of a linked data
   *
   * @return the preferences
   */
  @NotNull
  ValuesMap getPreferences();

  /**
   * Get the user that is linked to this data
   *
   * @return the user that is linked to this data
   */
  @Nullable
  UserData getLinkedUser();

  /**
   * Adds a permission to this linked data
   *
   * @param context the context to add the permission on
   * @param permission the permission to add
   */
  void addPermission(@NotNull String context, @NotNull T permission);

  /**
   * Removes a permission to this linked data
   *
   * @param context the context to remove the permission from
   * @param permission the permission to remove
   */
  void removePermission(@NotNull String context, @NotNull T permission);
}
