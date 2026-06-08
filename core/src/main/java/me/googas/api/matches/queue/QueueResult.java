package me.googas.api.matches.queue;

import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.api.matches.minecraft.MinecraftMatch;

public class QueueResult {

  @Getter @Setter private boolean cancelled = false;
  @NonNull @Getter @Setter private String reason = "No reason provided";
  private final MinecraftMatch match;

  /**
   * Create the queue result
   *
   * @param reason the reason to why the leaving o joining was not completed
   */
  public QueueResult(@NonNull String reason) {
    this.cancelled = true;
    this.reason = reason;
    this.match = null;
  }

  public QueueResult() {
    this.match = null;
  }

  /** Create the queue result */
  public QueueResult(MinecraftMatch match) {
    this.match = match;
  }

  @NonNull
  public Optional<? extends MinecraftMatch> getMatch() {
    return Optional.ofNullable(this.match);
  }
}
