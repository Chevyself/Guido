package me.googas.bot.api.events.data.links;

import lombok.NonNull;
import me.googas.bot.api.types.BotLinkable;

/** Called when linked data gets unloaded */
public class LinkedDataUnloadedEvent extends LinkedDataEvent {
  /**
   * Create the event
   *
   * @param data the data involved in the event
   */
  public LinkedDataUnloadedEvent(@NonNull BotLinkable data) {
    super(data);
  }
}
