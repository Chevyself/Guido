package me.googas.bot.api.events.data.links;

import lombok.Getter;
import lombok.NonNull;
import me.googas.bot.api.events.GuidoEvent;
import me.googas.bot.api.types.links.BotLinkable;
import me.googas.commons.builder.ToStringBuilder;

/** An event related to linked data */
public class LinkedDataEvent implements GuidoEvent {

  /** The linked data involved in the event */
  @NonNull @Getter private final BotLinkable data;

  /**
   * Create the event
   *
   * @param data the data involved in the event
   */
  public LinkedDataEvent(@NonNull BotLinkable data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("data", this.data).build();
  }
}
