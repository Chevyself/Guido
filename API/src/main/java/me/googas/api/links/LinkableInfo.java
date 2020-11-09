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
   * Get the type of the data
   *
   * @return the type of the data
   */
  @NotNull
  LinkableDataType getType();

  /**
   * Get how to identify the data
   *
   * @return how to identify the data
   */
  @NotNull
  ValuesMap getIdentification();

  /**
   * Get the data with the given values
   *
   * @return the data
   */
  @Nullable
  LinkableData getLink();
}
