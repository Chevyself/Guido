package me.googas.api.events.links;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.events.GuidoEvent;
import me.googas.api.links.Linkable;
import me.googas.commons.builder.ToStringBuilder;

/** An event related to linked data */
public class LinkableEvent implements GuidoEvent {

  /** The linked data involved in the event */
  @NonNull @Getter private final Linkable data;

  /**
   * Create the event
   *
   * @param data the data involved in the event
   */
  public LinkableEvent(@NonNull Linkable data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("data", this.data).build();
  }
}
