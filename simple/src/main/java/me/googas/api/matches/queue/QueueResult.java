package me.googas.api.matches.queue;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/** The result of {@link Queue#join(Queueable)} or {@link Queue#leave(Queueable)} */
public class QueueResult {

  @Getter @Setter private boolean cancelled = false;
  @NonNull @Getter @Setter private String reason = "No reason provided";

  /**
   * Create the queue result
   *
   * @param reason the reason to why the leaving o joining was not completed
   */
  public QueueResult(@NonNull String reason) {
    this.cancelled = true;
    this.reason = reason;
  }

  /** Create the queue result */
  public QueueResult() {}
}
