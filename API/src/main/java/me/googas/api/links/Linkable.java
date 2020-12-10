package me.googas.api.links;

import java.util.Collection;
import lombok.NonNull;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.lang.LocaleFile;
import me.googas.api.lang.Localized;
import me.googas.api.permissions.Permissible;
import me.googas.api.user.UserData;
import me.googas.api.utility.Stateable;
import me.googas.api.utility.ValuesMap;
import me.googas.commons.cache.Catchable;
import me.googas.commons.maps.Maps;

/** This object represents data that can been linked to an user */
public interface Linkable extends Permissible, Stateable, Catchable, Localized {

  /**
   * This method is used to compare this linkable data with a type and provided information
   *
   * @param type the type to compare
   * @param identification the identification to compare
   * @return true if it is the same type and the identification matches
   */
  default boolean compare(@NonNull LinkableType type, @NonNull ValuesMap identification) {
    return this.getInfo().compare(type, identification);
  }

  /**
   * @see #compare(LinkableType, ValuesMap)
   * @param info the information of the data comparing
   * @return true if it is the same type and the identification matches
   */
  default boolean compare(@NonNull LinkableInfo info) {
    return this.getInfo().compare(info);
  }

  /**
   * Get the linked data as a readable string
   *
   * @deprecated use {@link #getSingle()}
   * @param locale the locale that needs to read it
   * @return the readable string
   */
  @NonNull
  String getReadable(LocaleFile locale);

  /**
   * @see #getLinks() this will get only the links of certain type
   * @param types the types of links to get
   * @return the links
   */
  @NonNull
  Collection<Linkable> getLinks(@NonNull LinkableType... types);

  /**
   * @see #compare(LinkableType, ValuesMap)
   * @param data the other data comparing
   * @return true if it is the same type and the identification matches
   */
  default boolean compare(@NonNull Linkable data) {
    if (this == data) return true;
    return this.compare(data.getInfo());
  }

  /**
   * Set the linked user to this data
   *
   * @param user the new linked user
   */
  void setLinkedUser(UserData user);

  /**
   * Get the map which is used to identify the user. {@link #getIdentification()} can contain
   * multiple identifications but this one is of objects that wont change: such as an uuid in
   * minecraft
   *
   * @return the identification map
   */
  @NonNull
  default ValuesMap getIdentificationMap() {
    switch (this.getType()) {
      case MINECRAFT:
        return new SimpleValuesMap(Maps.singleton("uuid", this.getTrimmedUniqueId()));
      case DISCORD:
      case DISCORD_GUILD:
        return this.getIdentification();
      default:
        throw new IllegalStateException(this.getType() + " does not have an identification map");
    }
  }

  /**
   * Get the trimmed uuid of the {@link LinkableType#MINECRAFT}
   *
   * @return the trimmed uuid
   */
  default String getTrimmedUniqueId() {
    return this.getIdentification().get("uuid", String.class);
  }

  /**
   * Get this linked data as a single way to identify it. For example in the case of discord it will
   * be the tag, for minecraft its nickname
   *
   * @return a simple way to identify the data
   */
  @NonNull
  String getSingle();

  /**
   * Get the id of the user that is linked to this data
   *
   * @return the id of the user that is linked to this data
   */
  String getLinkedUserId();

  /**
   * Get the user that is linked to this data
   *
   * @return the user that is linked to this data
   */
  UserData getLinkedUser();

  /**
   * Get the preferences of a linked data
   *
   * @return the preferences
   */
  @NonNull
  ValuesMap getPreferences();

  /**
   * Get this linked data but only the type and identification
   *
   * @return the data as uncompleted
   */
  @NonNull
  LinkableInfo getInfo();

  /**
   * Set the total of credits that the linkable has
   *
   * @param value the new value of credits
   */
  void setCredits(float value);

  /**
   * Get all the links to this data. This will look for other links of {@link #getLinkedUser()}
   *
   * @return the collection of connected links
   */
  @NonNull
  Collection<Linkable> getLinks();

  /**
   * Get how this linked data is identified
   *
   * @return the identification of the data
   */
  @NonNull
  ValuesMap getIdentification();

  /**
   * Get the type of linked data
   *
   * @return the type of linked data
   */
  @NonNull
  LinkableType getType();

  /**
   * Get the total of credits that the linkable has
   *
   * @return the credits
   */
  float getCredits();

  /**
   * Get whether this data is linked to an user
   *
   * @return true if the data is linked
   */
  default boolean isLinked() {
    return this.getLinkedUser() != null;
  }
}
