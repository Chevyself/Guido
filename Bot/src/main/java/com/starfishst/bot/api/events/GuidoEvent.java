package com.starfishst.bot.api.events;

import com.starfishst.bot.Guido;
import me.googas.commons.events.Event;

/** This object represents an event called by the bot Guido */
public interface GuidoEvent extends Event {

  /**
   * Calls this event. This will get all the listeners for the event and call it for each of them
   */
  default void call() {
    Guido.call(this);
  }
}
