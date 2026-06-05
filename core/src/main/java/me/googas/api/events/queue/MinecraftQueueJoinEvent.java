package me.googas.api.events.queue;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.matches.queue.MinecraftQueue;

/** Called when data joins a queue */
public class MinecraftQueueJoinEvent extends MinecraftQueueEvent {

  @NonNull @Getter private final MinecraftLinkable minecraft;

  /**
   * Create the event
   *
   * @param queue the queue involved in the event
   * @param minecraft the data that joined the queue
   */
  public MinecraftQueueJoinEvent(
      @NonNull MinecraftQueue queue, @NonNull MinecraftLinkable minecraft) {
    super(queue);
    this.minecraft = minecraft;
  }
}
