package me.googas.api.matches.queue;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/** The result of {@link Queue#join(Queueable)} or {@link Queue#leave(Queueable)} */
public class QueueResult {

  @NonNull private final QueueResult.ActionType type;
  @Getter @Setter private boolean cancelled = false;
  @NonNull @Getter @Setter private String reason = "No reason provided";

  /**
   * Create the queue result
   *
   * @param type the type of queue action
   * @param reason the reason to why the leaving o joining was not completed
   */
  public QueueResult(@NonNull ActionType type, @NonNull String reason) {
    this.type = type;
    this.cancelled = true;
    this.reason = reason;
  }

  /**
   * Create the queue result
   *
   * @param type the type of queue action
   */
  public QueueResult(@NonNull ActionType type) {
    this.type = type;
  }

  public enum ActionType {
    /** Represents the queueable leaving the queue */
    LEAVE,
    /** Represents the queueable joining the queue */
    JOIN
  }
}
