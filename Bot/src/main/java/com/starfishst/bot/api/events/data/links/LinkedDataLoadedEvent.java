package com.starfishst.bot.api.events.data.links;

import com.starfishst.bot.api.data.BotLinkedData;
import org.jetbrains.annotations.NotNull;

/** Called when linked data gets loaded */
public class LinkedDataLoadedEvent extends LinkedDataEvent {
  /**
   * Create the event
   *
   * @param data the data involved in the event
   */
  public LinkedDataLoadedEvent(@NotNull BotLinkedData data) {
    super(data);
  }
}
