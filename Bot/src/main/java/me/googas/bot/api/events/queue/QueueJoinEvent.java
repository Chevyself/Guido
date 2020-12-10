package me.googas.bot.api.events.queue;

import lombok.NonNull;
import me.googas.api.links.LinkableInfo;
import me.googas.api.matches.Queue;

/** Called when data joins a queue */
public class QueueJoinEvent extends QueueEvent {

  /** The data that joined the queue */
  @NonNull private final LinkableInfo data;

  /**
   * Create the event
   *
   * @param queue the queue involved in the event
   * @param data the data that joined the queue
   */
  public QueueJoinEvent(@NonNull Queue queue, @NonNull LinkableInfo data) {
    super(queue);
    this.data = data;
  }

  /**
   * Get the data that joined the queue
   *
   * @return the data that joined the queue
   */
  @NonNull
  public LinkableInfo getData() {
    return this.data;
  }
}
