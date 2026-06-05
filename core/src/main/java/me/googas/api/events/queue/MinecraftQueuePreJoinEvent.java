package me.googas.api.events.queue;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.api.events.GuidoCancellable;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.matches.queue.MinecraftQueue;

/** Called before someone joins a queue and is not already on it */
public class MinecraftQueuePreJoinEvent extends MinecraftQueueEvent implements GuidoCancellable {

  @NonNull @Getter private final MinecraftLinkable minecraft;
  @NonNull @Getter @Setter private String reason;
  private boolean cancelled;

  /**
   * Create the event
   *
   * @param queue the queue involved in the event
   * @param minecraft the minecraft that joined the queue
   * @param reason the reason why the event is cancelled
   * @param cancelled whether the event is cancelled
   */
  private MinecraftQueuePreJoinEvent(
      @NonNull MinecraftQueue queue,
      @NonNull MinecraftLinkable minecraft,
      @NonNull String reason,
      boolean cancelled) {
    super(queue);
    this.minecraft = minecraft;
    this.reason = reason;
    this.cancelled = cancelled;
  }

  /**
   * Create the event
   *
   * @param queue the queue involved in the event
   * @param minecraft the minecraft that joined the queue
   */
  public MinecraftQueuePreJoinEvent(
      @NonNull MinecraftQueue queue, @NonNull MinecraftLinkable minecraft) {
    this(queue, minecraft, "No reason provided", false);
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
