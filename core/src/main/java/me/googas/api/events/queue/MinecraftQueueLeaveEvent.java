package me.googas.api.events.queue;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.matches.queue.MinecraftQueue;

/** Called when an user leaves the queue */
public class MinecraftQueueLeaveEvent extends MinecraftQueueEvent {

  /** The data that left the queue */
  @NonNull @Getter private final MinecraftLinkable minecraft;

  /**
   * Create the event
   *
   * @param queue the queue involved in the event
   * @param minecraft the data that left the queue
   */
  public MinecraftQueueLeaveEvent(
      @NonNull MinecraftQueue queue, @NonNull MinecraftLinkable minecraft) {
    super(queue);
    this.minecraft = minecraft;
  }
}
