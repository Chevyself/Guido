package me.googas.api;

import lombok.NonNull;
import me.googas.commons.cache.Catchable;

public interface GuidoCatchable extends Catchable {

  /**
   * Adds this catchable in cache
   *
   * @return this same object instance
   */
  @NonNull
  GuidoCatchable cache();

  /**
   * Unloads this object from cache
   *
   * @param onRemove whether to call the method on remove
   */
  default void unload(boolean onRemove) {}
}
