package com.starfishst.guido.api.data.links;

import com.starfishst.guido.api.data.Permissible;
import com.starfishst.guido.api.data.Stateable;
import com.starfishst.guido.api.data.UserData;
import com.starfishst.guido.api.data.ValuesMap;
import com.starfishst.guido.api.data.lang.LocaleFile;
import me.googas.commons.cache.ICatchable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** This object represents data that has been linked to an user */
public interface LinkedData extends Permissible, Stateable, ICatchable {

  /**
   * Get the linked data as a readable string
   *
   * @param locale the locale that needs to read it
   * @return the readable string
   */
  @NotNull
  String getReadable(LocaleFile locale);

  /**
   * Get this linked data as a single way to identify it. For example in the case of discord it will
   * be the tag, for minecraft its nickname
   *
   * @return a simple way to identify the data
   */
  @NotNull
  String getSingle();

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
   * Get the preferences of a linked data
   *
   * @return the preferences
   */
  @NotNull
  ValuesMap getPreferences();

  /**
   * Get how this linked data is identified
   *
   * @return the identification of the data
   */
  @NotNull
  ValuesMap getIdentification();

  /**
   * Get the id of the user that is linked to this data
   *
   * @return the id of the user that is linked to this data
   */
  @Nullable
  String getLinkedUserId();

  /**
   * Get the user that is linked to this data
   *
   * @return the user that is linked to this data
   */
  @Nullable
  UserData getLinkedUser();

  /**
   * Get this linked data but only the type and identification
   *
   * @return the data as uncompleted
   */
  @NotNull
  LinkedInfo getInfo();

  /**
   * Get whether this data is linked to an user
   *
   * @return true if the data is linkeed
   */
  boolean isLinked();
}
