package me.googas.api.matches;

import java.util.Collection;
import me.googas.api.links.LinkableInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A queue is joined by players to start playing */
public interface Queue {

  /**
   * Makes the linked info join the queue
   *
   * @param data the data that will join the queue
   * @return whether the data joined
   */
  boolean join(@NotNull LinkableInfo data);

  /**
   * Leaves the queue for certain linked data
   *
   * @param data the data leaving the queue
   * @return if the data left the queue
   */
  boolean leave(@NotNull LinkableInfo data);

  /**
   * Get whether someone is waiting in the queue
   *
   * @param data the data to check if it is waiting
   * @return true if the data is waiting inside this queue
   */
  default boolean isWaiting(@NotNull LinkableInfo data) {
    return this.getWaiting().contains(data);
  }

  /**
   * Check whether the queue is ready for a match. If the match is ready a new match will be started
   *
   * @return the match if the queue is ready
   */
  @Nullable
  Match checkReady();

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
  @NotNull
  Collection<LinkableInfo> getWaiting();

  /**
   * Get the ladder that this queue is playing
   *
   * @return the ladder
   */
  @NotNull
  String getLadderName();
}
