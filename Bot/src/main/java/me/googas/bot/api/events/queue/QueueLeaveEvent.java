package me.googas.bot.api.events.queue;

import me.googas.api.links.LinkedInfo;
import me.googas.api.matches.Queue;
import org.jetbrains.annotations.NotNull;

/** Called when an user leaves the queue */
public class QueueLeaveEvent extends QueueEvent {

  /** The data that left the queue */
  @NotNull private final LinkedInfo data;

  /**
   * Create the event
   *
   * @param queue the queue involved in the event
   * @param data the data that left the queue
   */
  public QueueLeaveEvent(@NotNull Queue queue, @NotNull LinkedInfo data) {
    super(queue);
    this.data = data;
  }

  /**
   * Get the data that left the queue
   *
   * @return the data that left the queue
   */
  @NotNull
  public LinkedInfo getData() {
    return this.data;
  }
}
