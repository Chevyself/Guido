package com.starfishst.bot.api.events.data.links;

import com.starfishst.bot.api.data.loader.BotLinkedData;
import org.jetbrains.annotations.NotNull;

/** Called when linked data gets unloaded */
public class LinkedDataUnloadedEvent extends LinkedDataEvent {
  /**
   * Create the event
   *
   * @param data the data involved in the event
   */
  public LinkedDataUnloadedEvent(@NotNull BotLinkedData data) {
    super(data);
  }
}
