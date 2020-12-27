package me.googas.api.user;

import java.util.Collection;
import lombok.NonNull;
import me.googas.api.ValuesMap;
import me.googas.api.lang.Localized;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableType;
import me.googas.commons.cache.Catchable;

/** The data of a discord user not required to be in a guild */
public interface UserData extends Catchable, Localized {

  /**
   * Get the unique id of the user
   *
   * @return the unique id of the user
   */
  @NonNull
  String getId();

  /**
   * Get the preferences of the user
   *
   * @return the preferences of the user
   */
  @NonNull
  ValuesMap getPreferences();

  /**
   * Get a link to this user from the given type
   *
   * @param type the type of the link to get
   * @return the link if found else null
   */
  Linkable getLink(@NonNull LinkableType type);

  /**
   * Get the linkable data that is linked to this user
   *
   * @return the linkable data as a collection
   */
  @NonNull
  Collection<Linkable> getLinks();

  @Override
  @NonNull
  default String getLang() {
    return this.getPreferences().getOr("lang", String.class, "en");
  }

  @Override
  default void setLang(@NonNull String lang) {
    this.getPreferences().put("lang", lang);
  }
}
