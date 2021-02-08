package me.googas.bot.core.matches.queue;

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import me.googas.api.events.queue.QueueJoinEvent;
import me.googas.api.events.queue.QueuePreJoinEvent;
import me.googas.api.matches.AbstractMatch;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.queue.Queue;
import me.googas.api.matches.queue.QueueResult;
import me.googas.api.matches.queue.Queueable;
import me.googas.bot.api.Guido;
import me.googas.commons.Validate;
import me.googas.commons.builder.ToStringBuilder;

/** An implementation for queue */
// TODO there's many localization that needs to be done in this class
public class GuidoQueue implements Queue {

  private final long guildId;
  private final String ladder;
  private final List<Queueable> waiting = new ArrayList<>();

  /**
   * Create the queue
   *
   * @param guildId the id where this queue was created
   * @param ladder the names of the ladders waiting for queue
   */
  public GuidoQueue(long guildId, @NonNull String ladder) {
    this.guildId = guildId;
    this.ladder = ladder;
  }

  /**
   * Get the ladder where this queue is happening
   *
   * @return the ladder
   */
  @NonNull
  public Ladder getLadder() {
    return Validate.notNull(
        Guido.getHandlers().getDiscordLoader().getGuild(this.guildId).getLadder(this.ladder),
        "Ladder was deleted?");
  }

  @NonNull
  public String getLadderName() {
    return this.ladder;
  }

  @Override
  public long getGuildId() {
    return this.guildId;
  }

  @Override
  public QueueResult join(@NonNull Queueable data) {
    if (!this.isWaiting(data) && new QueuePreJoinEvent(this, data).callAndGet()) {
      this.getWaiting().add(data);
      new QueueJoinEvent(this, data).call();
      return new QueueResult();
    }
    return new QueueResult("You're already waiting in this queue");
  }

  @Override
  public QueueResult leave(@NonNull Queueable data) {
    if (this.isWaiting(data)) {
      if (this.getWaiting().remove(data)) {
        return new QueueResult();
      } else {
        return new QueueResult("Could not leave the queue");
      }
    }
    return new QueueResult("You are not waiting in this queue");
  }

  @Override
  public AbstractMatch checkReady() {
    return null;
  }

  @NonNull
  public List<Queueable> getWaiting() {
    return this.waiting;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("guildId", this.guildId)
        .append("ladder", this.ladder)
        .append("waiting", this.waiting)
        .build();
  }
}
