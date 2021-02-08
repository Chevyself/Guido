package me.googas.api.events.links;

import lombok.NonNull;
import me.googas.api.links.Linkable;

/** Called when linked data gets unloaded */
public class LinkableUnloadedEvent extends LinkableEvent {
  /**
   * Create the event
   *
   * @param data the data involved in the event
   */
  public LinkableUnloadedEvent(@NonNull Linkable data) {
    super(data);
  }
}
