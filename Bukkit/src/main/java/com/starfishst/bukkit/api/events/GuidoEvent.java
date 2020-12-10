package com.starfishst.bukkit.api.events;

import com.starfishst.bukkit.api.Guido;
import lombok.NonNull;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/** An event called by {@link Guido} */
public class GuidoEvent extends Event {

  /** The list of handlers for the event */
  @NonNull private static final HandlerList handlers = new HandlerList();

  /** Calls the event */
  public void call() {
    Guido.call(this);
  }

  /**
   * Get the list of handlers for this event
   *
   * @return the list of handlers for this event
   */
  @NonNull
  public static HandlerList getHandlerList() {
    return GuidoEvent.handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return GuidoEvent.handlers;
  }
}
