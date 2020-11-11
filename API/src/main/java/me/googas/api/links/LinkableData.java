package me.googas.api.links;

import java.util.Collection;
import me.googas.api.lang.LocaleFile;
import me.googas.api.lang.Localized;
import me.googas.api.permissions.Permissible;
import me.googas.api.user.UserData;
import me.googas.api.utility.ValuesMap;
import me.googas.commons.cache.Catchable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** This object represents data that can been linked to an user */
public interface LinkableData extends Permissible, Stateable, Catchable, Localized {

  /**
   * This method is used to compare this linkable data with a type and provided information
   *
   * @param type the type to compare
   * @param identification the identification to compare
   * @return true if it is the same type and the identification matches
   */
  default boolean compare(@NotNull LinkableDataType type, @NotNull ValuesMap identification) {
    return this.getInfo().compare(type, identification);
  }

  /**
   * @see #compare(LinkableDataType, ValuesMap)
   * @param info the information of the data comparing
   * @return true if it is the same type and the identification matches
   */
  default boolean compare(@NotNull LinkableInfo info) {
    return this.getInfo().compare(info);
  }

  /**
   * Get the linked data as a readable string
   *
   * @deprecated use {@link #getSingle()}
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
   * @see #getLinks() this will get only the links of certain type
   * @param types the types of links to get
   * @return the links
   */
  @NotNull
  Collection<LinkableData> getLinks(@NotNull LinkableDataType... types);

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
   * @see #compare(LinkableDataType, ValuesMap)
   * @param data the other data comparing
   * @return true if it is the same type and the identification matches
   */
  default boolean compare(@NotNull LinkableData data) {
    if (this == data) {
      return true;
    } else {
      return this.compare(data.getInfo());
    }
  }

  /**
   * Get this linked data but only the type and identification
   *
   * @return the data as uncompleted
   */
  @NotNull
  LinkableInfo getInfo();

  /**
   * Get all the links to this data. This will look for other links of {@link #getLinkedUser()}
   *
   * @return the collection of connected links
   */
  @NotNull
  Collection<LinkableData> getLinks();

  /**
   * Get the type of linked data
   *
   * @return the type of linked data
   */
  @NotNull
  LinkableDataType getType();

  /**
   * Get how this linked data is identified
   *
   * @return the identification of the data
   */
  @NotNull
  ValuesMap getIdentification();

  /**
   * Get whether this data is linked to an user
   *
   * @return true if the data is linked
   */
  default boolean isLinked() {
    return this.getLinkedUser() != null;
  }
}
