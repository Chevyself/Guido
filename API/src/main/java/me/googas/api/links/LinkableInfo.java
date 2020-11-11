package me.googas.api.links;

import me.googas.api.utility.ValuesMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * LinkedInf represents the linked data as an object to get it. This means that this contains the
 * way to identify it and the type
 */
public interface LinkableInfo {

  /**
   * This method is used to compare this linkable data with a type and provided information
   *
   * @param type the type to compare
   * @param identification the identification to compare
   * @return true if it is the same type and the identification matches
   */
  default boolean compare(@NotNull LinkableDataType type, @NotNull ValuesMap identification) {
    if (this.getType() == type) {
      switch (type) {
        case DISCORD_GUILD:
          return this.getIdentification()
                  .getOr("id", Long.class, -1L)
                  .equals(identification.get("id", Long.class))
              && this.getIdentification()
                  .getOr("guildId", Long.class, -1L)
                  .equals(identification.get("guildId", Long.class));
        case DISCORD:
          return this.getIdentification()
              .getOr("id", Long.class, -1L)
              .equals(identification.get("id", Long.class));
        case MINECRAFT:
          String thatUuid = identification.get("uuid", String.class);
          String thatNickname = identification.get("uuid", String.class);
          return this.getIdentification().getOr("uuid", String.class, "").equals(thatUuid)
              || this.getIdentification()
                  .getOr("nickname", String.class, "")
                  .equalsIgnoreCase(thatNickname);
        default:
          throw new IllegalArgumentException(type + " is not valid to be compared");
      }
    }
    return false;
  }

  /**
   * @see #compare(LinkableDataType, ValuesMap)
   * @param info the information of the data comparing
   * @return true if it is the same type and the identification matches
   */
  default boolean compare(@NotNull LinkableInfo info) {
    if (this == info) {
      return true;
    } else {
      return this.compare(info.getType(), info.getIdentification());
    }
  }

  /**
   * Get the data with the given values
   *
   * @return the data
   */
  @Nullable
  LinkableData getLink();

  /**
   * @see #compare(LinkableDataType, ValuesMap)
   * @param data the other data comparing
   * @return true if it is the same type and the identification matches
   */
  default boolean compare(@NotNull LinkableData data) {
    return this.compare(data.getInfo());
  }

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
}
