package me.googas.api.links;

import lombok.NonNull;
import me.googas.api.ValuesMap;
import me.googas.api.matches.queue.Queueable;

/**
 * LinkedInf represents the linked data as an object to get it. This means that this contains the
 * way to identify it and the type
 */
public interface LinkableInfo extends Queueable {

  /**
   * This method is used to compare this linkable data with a type and provided information
   *
   * @param type the type to compare
   * @param identification the identification to compare
   * @return true if it is the same type and the identification matches
   */
  default boolean compare(@NonNull LinkableType type, @NonNull ValuesMap identification) {
    if (this.getType() != type) return false;
    return this.getIdentification().isSimilar(identification.getMap());
  }

  /**
   * @see #compare(LinkableType, ValuesMap)
   * @param info the information of the data comparing
   * @return true if it is the same type and the identification matches
   */
  default boolean compare(@NonNull LinkableInfo info) {
    if (this == info) {
      return true;
    } else {
      return this.compare(info.getType(), info.getIdentification());
    }
  }

  /**
   * @see #compare(LinkableType, ValuesMap)
   * @param data the other data comparing
   * @return true if it is the same type and the identification matches
   */
  default boolean compare(@NonNull Linkable data) {
    return this.compare(data.getInfo());
  }

  /**
   * Get the data with the given values
   *
   * @return the data
   */
  Linkable getLink();

  /**
   * Get the type of linked data
   *
   * @return the type of linked data
   */
  @NonNull
  LinkableType getType();

  /**
   * Get how this linked data is identified
   *
   * @return the identification of the data
   */
  @NonNull
  ValuesMap getIdentification();
}
