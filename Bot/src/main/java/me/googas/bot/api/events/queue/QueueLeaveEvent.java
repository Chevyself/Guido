package me.googas.bot.api.events.queue;

import lombok.NonNull;
import me.googas.api.links.LinkableInfo;
import me.googas.api.matches.Queue;

/** Called when an user leaves the queue */
public class QueueLeaveEvent extends QueueEvent {

  /** The data that left the queue */
  @NonNull private final LinkableInfo data;

  /**
   * Create the event
   *
   * @param queue the queue involved in the event
   * @param data the data that left the queue
   */
  public QueueLeaveEvent(@NonNull Queue queue, @NonNull LinkableInfo data) {
    super(queue);
    this.data = data;
  }

  /**
   * Get the data that left the queue
   *
   * @return the data that left the queue
   */
  @NonNull
  public LinkableInfo getData() {
    return this.data;
  }
}
