package me.googas.api.events;

import me.googas.api.API;
import me.googas.starbox.events.Cancellable;
import me.googas.starbox.events.Event;

/** This class represents an event that can be cancelled */
public interface GuidoCancellable extends Cancellable {

  /**
   * Calls an event. As in {@link GuidoEvent#call()} but returns whether it was cancelled. true if
   * it was not cancelled
   *
   * @return true if the event was cancelled
   * @throws IllegalArgumentException cancellable is not an instance of {@link Event}
   */
  default boolean callAndGet() {
    return API.getListenerManager().callAndGet(this);
  }

  default boolean not() {
    // TRUE if the event was not cancelled
    return !API.getListenerManager().callAndGet(this);
  }
}
