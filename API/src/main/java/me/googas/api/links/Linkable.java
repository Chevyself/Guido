package me.googas.api.links;

import java.util.Collection;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.GuidoCatchable;
import me.googas.api.Stateable;
import me.googas.api.ValuesMap;
import me.googas.api.lang.LocaleFile;
import me.googas.api.lang.Localized;
import me.googas.api.links.ref.DiscordLinkable;
import me.googas.api.links.ref.MinecraftLinkable;
import me.googas.api.matches.team.Team;
import me.googas.api.permissions.Permissible;
import me.googas.api.user.UserData;
import me.googas.commons.Validate;

/** This object represents data that can been linked to an user */
public interface Linkable extends Permissible, Stateable, GuidoCatchable, Localized {

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
   * This method is used to compare this linkable data with a type and provided information
   *
   * @param type the type to compare
   * @param identification the identification to compare
   * @param recognition the recognition map to match
   * @return true if it is the same type and the identification matches
   */
  default boolean compare(
      @NonNull LinkableType type,
      @NonNull ValuesMap identification,
      @NonNull ValuesMap recognition) {
    if (this.getType() != type) return false;
    return this.getIdentification().isSimilar(identification.getMap())
        || this.getRecognition().matches(recognition.getMap());
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
  @Deprecated
  default ValuesMap getIdentificationMap() {
    return this.getIdentification();
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

  @Nullable
  default DiscordLinkable toDiscordRef() {
    if (this.getType() != LinkableType.DISCORD) {
      UserData user = this.getLinkedUser();
      if (user != null) {
        Linkable link = user.getLink(LinkableType.DISCORD);
        if (link != null) return link.toDiscordRef();
      }
      return null;
    }
    return new DiscordLinkable(this);
  }

  @NonNull
  default DiscordLinkable requireDiscordRef() {
    return Validate.notNull(this.toDiscordRef(), "Could not get discord ref");
  }

  @NonNull
  default MinecraftLinkable requireMinecraftRef() {
    return Validate.notNull(this.toMinecraftRef(), "Could not get minecraft ref");
  }

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
  // TODO this must be a different class
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
   * This is a map that contains more ways to identify a link which can be different such as a tag
   * in discord or a name in minecraft even the ip of minecraft
   *
   * @return the recognition map
   */
  @NonNull
  ValuesMap getRecognition();

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

  /**
   * Get the team in which the link is on
   *
   * @return the team in which it is on
   */
  Team getTeam();

  @Override
  @NonNull
  default String getLang() {
    return this.getPreferences().getOr("lang", String.class, "en");
  }

  @Override
  default void setLang(@NonNull String lang) {
    this.getPreferences().put("lang", lang);
  }

  @Nullable
  default MinecraftLinkable toMinecraftRef() {
    if (this.getType() != LinkableType.MINECRAFT) {
      UserData user = this.getLinkedUser();
      if (user != null) {
        Linkable link = user.getLink(LinkableType.MINECRAFT);
        if (link != null) return link.toMinecraftRef();
      }
      return null;
    }
    return new MinecraftLinkable(this);
  }

  /**
   * Get the user that is linked to this data
   *
   * @return the user that is linked to this data
   */
  @Nullable
  UserData getLinkedUser();
}
