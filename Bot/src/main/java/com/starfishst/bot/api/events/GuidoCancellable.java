package com.starfishst.bot.api.events;

import com.starfishst.bot.Guido;
import me.googas.commons.events.Cancellable;
import me.googas.commons.events.Event;

/** This class represents an event that can be cancelled */
public interface GuidoCancellable extends Cancellable {

  /**
   * Calls an event. As in {@link GuidoEvent#call()} but returns whether it was cancelled
   *
   * @return true if the event was cancelled
   * @throws IllegalArgumentException cancellable is not an instance of {@link Event}
   */
  default boolean callAndGet() {
    return Guido.call(this);
  }
}
