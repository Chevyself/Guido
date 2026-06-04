package me.googas.api.events.queue;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.events.GuidoEvent;
import me.googas.api.matches.queue.Queue;

/** An event that is related to a queue */
public class QueueEvent implements GuidoEvent {

  /** The queue involved in the event */
  @NonNull @Getter private final Queue queue;

  /**
   * Create the event
   *
   * @param queue the queue involved in the event
   */
  public QueueEvent(@NonNull Queue queue) {
    this.queue = queue;
  }
}
