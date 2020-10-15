package com.starfishst.bot.api.events.data.links;

import com.starfishst.bot.api.data.loader.BotLinkedData;
import com.starfishst.bot.api.events.GuidoEvent;
import org.jetbrains.annotations.NotNull;

/** An event related to linked data */
public class LinkedDataEvent implements GuidoEvent {

  /** The data involved in the event */
  @NotNull private final BotLinkedData data;

  /**
   * Create the event
   *
   * @param data the data involved in the event
   */
  public LinkedDataEvent(@NotNull BotLinkedData data) {
    this.data = data;
  }

  /**
   * Get the data involved in the event
   *
   * @return the data involved in the event
   */
  @NotNull
  public BotLinkedData getData() {
    return this.data;
  }
}
