package com.starfishst.bot.api.events.queue;

import me.googas.api.links.LinkedInfo;
import me.googas.api.matches.Queue;
import org.jetbrains.annotations.NotNull;

/** Called when data joins a queue */
public class QueueJoinEvent extends QueueEvent {

  /** The data that joined the queue */
  @NotNull private final LinkedInfo data;

  /**
   * Create the event
   *
   * @param queue the queue involved in the event
   * @param data the data that joined the queue
   */
  public QueueJoinEvent(@NotNull Queue queue, @NotNull LinkedInfo data) {
    super(queue);
    this.data = data;
  }

  /**
   * Get the data that joined the queue
   *
   * @return the data that joined the queue
   */
  @NotNull
  public LinkedInfo getData() {
    return this.data;
  }
}
