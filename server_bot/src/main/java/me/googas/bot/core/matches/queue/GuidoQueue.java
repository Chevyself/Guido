package me.googas.bot.core.matches.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.events.queue.MinecraftQueueJoinEvent;
import me.googas.api.events.queue.MinecraftQueuePreJoinEvent;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.minecraft.MinecraftMatch;
import me.googas.api.matches.minecraft.MinecraftMatchTeamMember;
import me.googas.api.matches.queue.MinecraftQueue;
import me.googas.api.matches.queue.QueueResult;
import me.googas.bot.GuidoBotRuntime;
import me.googas.starbox.events.ListenerManager;
import org.jetbrains.annotations.NotNull;

/** An implementation for queue */
// TODO there's many localization that needs to be done in this class
public class GuidoQueue implements MinecraftQueue {

  private final String ladder;
  @NonNull protected final GuidoBotRuntime runtime;
  @NonNull @Getter private final List<MinecraftLinkable> waiting = new ArrayList<>();

  /**
   * Create the queue
   *
   * @param ladder the names of the ladders waiting for queue
   */
  public GuidoQueue(@NonNull String ladder, @NonNull GuidoBotRuntime runtime) {
    this.ladder = ladder;
    this.runtime = runtime;
  }

  /**
   * Get the ladder where this queue is happening
   *
   * @return the ladder
   */
  @NonNull
  public Ladder getLadder() {
    return Objects.requireNonNull(
        runtime.getLoader().getGuidoGuildLoader().getGuild().getLadder(this.ladder),
        "Ladder was deleted?");
  }

  @NonNull
  public String getLadderName() {
    return this.ladder;
  }

  @Override
  public void leave(@NonNull MinecraftMatchTeamMember member) {}

  @Override
  public @NonNull QueueResult join(@NonNull MinecraftLinkable minecraft) {
    if (this.isWaiting(minecraft)) return new QueueResult("You're already waiting in this queue");

    MinecraftQueuePreJoinEvent event = new MinecraftQueuePreJoinEvent(this, minecraft);
    ListenerManager listeners = runtime.getListeners();
    boolean cancelled = listeners.callAndGet(event);
    if (cancelled) return new QueueResult(event.getReason());
    this.getWaiting().add(minecraft);
    listeners.call(new MinecraftQueueJoinEvent(this, minecraft));
    return new QueueResult();
  }

  @Override
  public @NonNull QueueResult leave(@NonNull MinecraftLinkable minecraft) {
    if (this.isWaiting(minecraft)) {
      if (this.getWaiting().remove(minecraft)) {
        return new QueueResult();
      } else {
        return new QueueResult("Could not leave the queue");
      }
    }
    return new QueueResult("You are not waiting in this queue");
  }

  @NotNull
  @Override
  public Optional<MinecraftMatch> checkReady() {
    return Optional.empty();
  }

  @Override
  public String toString() {
    return "GuidoQueue{"
        + "ladder='"
        + ladder
        + '\''
        + ", runtime="
        + runtime
        + ", waiting="
        + waiting
        + '}';
  }
}
