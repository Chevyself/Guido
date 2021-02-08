package me.googas.api.matches.queue;

import java.util.Collection;
import lombok.NonNull;
import me.googas.api.matches.AbstractMatch;

/** A queue is joined by players to start playing */
public interface Queue {

  /**
   * Makes the linked info join the queue
   *
   * @param queueable the queueable that will join the queue
   * @return whether the queueable joined
   */
  QueueResult join(@NonNull Queueable queueable);

  /**
   * Leaves the queue for certain linked queueable
   *
   * @param queueable the queueable leaving the queue
   * @return if the queueable left the queue
   */
  QueueResult leave(@NonNull Queueable queueable);

  /**
   * Get whether someone is waiting in the queue
   *
   * @param queueable the queueable to check if it is waiting
   * @return true if the queueable is waiting inside this queue
   */
  default boolean isWaiting(@NonNull Queueable queueable) {
    return this.getWaiting().contains(queueable);
  }

  /**
   * Check whether the queue is ready for a match. If the match is ready a new match will be started
   *
   * @return the match if the queue is ready
   */
  AbstractMatch checkReady();

  /**
   * Get the id of the guild where this queue is happening
   *
   * @return the id of the guild
   */
  long getGuildId();

  /**
   * Get the linked data of the users that are waiting
   *
   * @return the linked data
   */
  @NonNull
  Collection<Queueable> getWaiting();

  /**
   * Get the ladder that this queue is playing
   *
   * @return the ladder
   */
  @NonNull
  String getLadderName();
}
