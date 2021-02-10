package me.googas.api;

import lombok.NonNull;
import me.googas.commons.cache.Catchable;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;

public interface GuidoCatchable extends Catchable {

  /**
   * Adds this catchable in cache
   *
   * @return this same object instance
   */
  @NonNull
  default GuidoCatchable cache() {
    API.getCache().add(this);
    return this;
  }

  /**
   * Unloads this object from cache
   *
   * @param onRemove whether to call the method on remove
   */
  default void unload(boolean onRemove) throws Throwable {
    if (onRemove) this.onRemove();
    API.getCache().remove(this);
  }

  @Override
  default @NonNull Time getToRemove() {
    return new Time(3, Unit.MINUTES);
  }
}
