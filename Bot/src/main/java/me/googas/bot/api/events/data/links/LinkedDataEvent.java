package me.googas.bot.api.events.data.links;

import me.googas.bot.api.events.GuidoEvent;
import me.googas.bot.api.types.BotLinkedData;
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

  @Override
  public String toString() {
    return "LinkedDataEvent{" + "data=" + this.data + '}';
  }
}
