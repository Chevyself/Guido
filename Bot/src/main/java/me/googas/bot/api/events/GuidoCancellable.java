package me.googas.bot.api.events;

import me.googas.bot.api.Guido;
import me.googas.commons.events.Cancellable;
import me.googas.commons.events.Event;

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
    return !Guido.getListenerManager().call(this);
  }
}
