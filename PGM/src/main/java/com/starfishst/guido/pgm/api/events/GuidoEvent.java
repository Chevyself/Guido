package com.starfishst.guido.pgm.api.events;

import com.starfishst.guido.pgm.GuidoPlugin;
import com.starfishst.guido.pgm.api.Guido;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/** An event called by {@link GuidoPlugin} */
public class GuidoEvent extends Event {

  /** The list of handlers for the event */
  @NotNull private static final HandlerList handlers = new HandlerList();

  /** Calls the event */
  public void call() {
    Guido.call(this);
  }

  /**
   * Get the list of handlers for this event
   *
   * @return the list of handlers for this event
   */
  @NotNull
  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
