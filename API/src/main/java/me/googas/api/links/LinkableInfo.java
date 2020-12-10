package me.googas.api.links;

import lombok.NonNull;
import me.googas.api.matches.Queueable;
import me.googas.api.utility.ValuesMap;

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
    switch (type) {
      case DISCORD_GUILD:
        return this.getIdentification()
                .getOr("id", Long.class, -1L)
                .equals(identification.get("id", Long.class))
            && this.getIdentification()
                .getOr("guild", Long.class, -1L)
                .equals(identification.get("guild", Long.class));
      case DISCORD:
        return this.getIdentification()
            .getOr("id", Long.class, -1L)
            .equals(identification.get("id", Long.class));
      case MINECRAFT:
        return this.getIdentification()
                .getOr("uuid", String.class, "")
                .equals(identification.get("uuid", String.class))
            || this.getIdentification()
                .getOr("nickname", String.class, "")
                .equalsIgnoreCase(identification.get("nickname", String.class));
      default:
        throw new IllegalArgumentException(type + " is not valid to be compared");
    }
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
