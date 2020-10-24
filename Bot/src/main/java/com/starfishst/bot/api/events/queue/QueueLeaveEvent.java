package com.starfishst.bot.api.events.queue;

import com.starfishst.guido.api.data.links.LinkedInfo;
import com.starfishst.guido.api.data.matches.Queue;
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
