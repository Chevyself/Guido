package me.googas.api.events.queue;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.queue.Queue;
import me.googas.api.matches.queue.Queueable;

/** Called when an user leaves the queue */
public class QueueLeaveEvent extends QueueEvent {

  /** The data that left the queue */
  @NonNull @Getter private final Queueable data;

  /**
   * Create the event
   *
   * @param queue the queue involved in the event
   * @param data the data that left the queue
   */
  public QueueLeaveEvent(@NonNull Queue queue, @NonNull Queueable data) {
    super(queue);
    this.data = data;
  }
}
