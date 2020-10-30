package me.googas.bot.api.events.queue;

import me.googas.api.matches.Queue;
import me.googas.bot.api.events.GuidoEvent;
import org.jetbrains.annotations.NotNull;

/** An event that is related to a queue */
public class QueueEvent implements GuidoEvent {

  /** The queue involved in the event */
  @NotNull private final Queue queue;

  /**
   * Create the event
   *
   * @param queue the queue involved in the event
   */
  public QueueEvent(@NotNull Queue queue) {
    this.queue = queue;
  }

  /**
   * Get the queue involved in the event
   *
   * @return the queue involved in the event
   */
  @NotNull
  public Queue getQueue() {
    return this.queue;
  }
}
