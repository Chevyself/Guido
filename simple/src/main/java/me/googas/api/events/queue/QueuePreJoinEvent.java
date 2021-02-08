package me.googas.api.events.queue;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.api.events.GuidoCancellable;
import me.googas.api.matches.queue.Queue;
import me.googas.api.matches.queue.Queueable;

/** Called before someone joins a queue and is not already on it */
public class QueuePreJoinEvent extends QueueEvent implements GuidoCancellable {

  @NonNull @Getter private final Queueable data;
  @NonNull @Getter @Setter private String reason;
  private boolean cancelled;

  /**
   * Create the event
   *
   * @param queue the queue involved in the event
   * @param queueable the queueable that joined the queue
   * @param reason the reason why the event is cancelled
   * @param cancelled whether the event is cancelled
   */
  private QueuePreJoinEvent(
      @NonNull Queue queue,
      @NonNull Queueable queueable,
      @NonNull String reason,
      boolean cancelled) {
    super(queue);
    this.data = queueable;
    this.reason = reason;
    this.cancelled = cancelled;
  }

  /**
   * Create the event
   *
   * @param queue the queue involved in the event
   * @param queueable the queueable that joined the queue
   */
  public QueuePreJoinEvent(@NonNull Queue queue, @NonNull Queueable queueable) {
    this(queue, queueable, "No reason provided", false);
  }

  @Override
  public void setCancelled(boolean b) {
    this.cancelled = b;
  }

  @Override
  public boolean isCancelled() {
    return this.cancelled;
  }
}
