package me.googas.api.events;

import me.googas.api.API;
import me.googas.commons.events.Event;

/** This object represents an event called by the bot GuidoBot */
public interface GuidoEvent extends Event {

  /**
   * Calls this event. This will getId all the listeners for the event and call it for each of them
   */
  default void call() {
    API.getListenerManager().call(this);
  }
}
