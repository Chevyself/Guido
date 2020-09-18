package com.starfishst.guido.api.data;

import java.util.HashMap;
import org.jetbrains.annotations.NotNull;

/** This means that the data can be linked with something else */
public interface Linkable {

  /**
   * Adds a link to this data
   *
   * @param key the key to know where is it linked from
   * @param value the value of the link
   */
  default void addLink(@NotNull String key, @NotNull String value) {
    this.getLinks().put(key, value);
  }

  /**
   * Removes a link from this linkable
   *
   * @param key the key of the linkable
   */
  default void removeLink(@NotNull String key) {
    this.getLinks().remove(key);
  }

  /**
   * This map contains anything to which this data is linked to
   *
   * @return the links for this data
   */
  @NotNull
  HashMap<String, String> getLinks();
}
