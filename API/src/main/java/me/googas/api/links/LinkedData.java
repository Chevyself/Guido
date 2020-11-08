package me.googas.api.links;

import java.util.Collection;
import java.util.Map;
import me.googas.api.lang.LocaleFile;
import me.googas.api.permissions.Permissible;
import me.googas.api.user.UserData;
import me.googas.api.utility.ValuesMap;
import me.googas.commons.cache.Catchable;
import me.googas.commons.maps.MapBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** This object represents data that has been linked to an user */
public interface LinkedData extends Permissible, Stateable, Catchable {

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
   * Send a message to this linked data
   *
   * @param message the message to send
   */
  void sendMessage(@NotNull String message);

  /**
   * Send a localized message using a given key
   *
   * @param key the key of the localized message
   */
  void sendLocalized(@NotNull String key);

  /**
   * Send a localized message using the given key and placeholders
   *
   * @param key the key of the localized message
   * @param placeholders the placeholders of the message
   */
  void sendLocalized(@NotNull String key, @NotNull Map<String, String> placeholders);

  /**
   * Send a localized message using the given key and placeholders
   *
   * @param key the key of the localized message
   * @param placeholders the placeholders of the message as a map builder
   */
  default void sendLocalized(
      @NotNull String key, @NotNull MapBuilder<String, String> placeholders) {
    this.sendLocalized(key, placeholders.build());
  }

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
   * @return true if the data is linked
   */
  boolean isLinked();

  /**
   * Get all the links to this data. This will look for other links of {@link #getLinkedUser()}
   *
   * @return the collection of connected links
   */
  @NotNull
  Collection<LinkedData> getLinks();

  /**
   * @see #getLinks() this will get only the links of certain type
   * @param types the types of links to get
   * @return the links
   */
  @NotNull
  Collection<LinkedData> getLinks(@NotNull LinkedDataType... types);
}
