package me.googas.api.events.queue;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.events.GuidoEvent;
import me.googas.api.matches.queue.MinecraftQueue;

/** An event that is related to a queue */
public class QueueEvent implements GuidoEvent {

  /** The queue involved in the event */
  @NonNull @Getter private final MinecraftQueue queue;

  /**
   * Create the event
   *
   * @param queue the queue involved in the event
   */
  public QueueEvent(@NonNull MinecraftQueue queue) {
    this.queue = queue;
  }
}
