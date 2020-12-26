package me.googas.bot.api.events.queue;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.queue.Queue;
import me.googas.api.matches.queue.Queueable;

/** Called when data joins a queue */
public class QueueJoinEvent extends QueueEvent {

  /** The data that joined the queue */
  @NonNull @Getter private final Queueable data;

  /**
   * Create the event
   *
   * @param queue the queue involved in the event
   * @param data the data that joined the queue
   */
  public QueueJoinEvent(@NonNull Queue queue, @NonNull Queueable data) {
    super(queue);
    this.data = data;
  }
}
