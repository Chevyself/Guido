package me.googas.bot.api.events.data.links;

import me.googas.bot.api.types.BotLinkable;
import org.jetbrains.annotations.NotNull;

/** Called when linked data gets unloaded */
public class LinkedDataUnloadedEvent extends LinkedDataEvent {
  /**
   * Create the event
   *
   * @param data the data involved in the event
   */
  public LinkedDataUnloadedEvent(@NotNull BotLinkable data) {
    super(data);
  }
}
