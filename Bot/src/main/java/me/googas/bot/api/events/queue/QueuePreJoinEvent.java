package me.googas.bot.api.events.queue;

import lombok.NonNull;
import me.googas.api.links.LinkableInfo;
import me.googas.api.matches.Queue;
import me.googas.bot.api.events.GuidoCancellable;

/** Called before someone joins a queue and is not already on it */
public class QueuePreJoinEvent extends QueueEvent implements GuidoCancellable {

  /** The information of the user joining the queue */
  @NonNull private final LinkableInfo data;

  /** Why was the join event cancelled */
  @NonNull private final String reason;

  /** Whether the event is cancelled */
  private boolean cancelled;

  /**
   * Create the event
   *
   * @param queue the queue involved in the event
   * @param data the data that joined the queue
   * @param reason the reason why the event is cancelled
   * @param cancelled whether the event is cancelled
   */
  private QueuePreJoinEvent(
      @NonNull Queue queue, @NonNull LinkableInfo data, @NonNull String reason, boolean cancelled) {
    super(queue);
    this.data = data;
    this.reason = reason;
    this.cancelled = cancelled;
  }

  /**
   * Create the event
   *
   * @param queue the queue involved in the event
   * @param data the data that joined the queue
   */
  public QueuePreJoinEvent(@NonNull Queue queue, @NonNull LinkableInfo data) {
    this(queue, data, "No reason provided", false);
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
