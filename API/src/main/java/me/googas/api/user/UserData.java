package me.googas.api.user;

import java.util.Collection;
import me.googas.api.lang.Localized;
import me.googas.api.links.Linkable;
import me.googas.api.utility.ValuesMap;
import me.googas.commons.cache.Catchable;
import org.jetbrains.annotations.NotNull;

/** The data of a discord user not required to be in a guild */
public interface UserData extends Catchable, Localized {

  /**
   * Get the unique id of the user
   *
   * @return the unique id of the user
   */
  @NotNull
  String getId();

  /**
   * Get the preferences of the user
   *
   * @return the preferences of the user
   */
  @NotNull
  ValuesMap getPreferences();

  /**
   * Get the linkable data that is linked to this user
   *
   * @return the linkable data as a collection
   */
  Collection<Linkable> getLinks();
}
