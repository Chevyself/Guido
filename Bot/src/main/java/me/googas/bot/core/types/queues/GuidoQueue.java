package me.googas.bot.core.types.queues;

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.matches.Ladder;
import me.googas.api.matches.Queue;
import me.googas.api.matches.Queueable;
import me.googas.bot.api.events.queue.QueueJoinEvent;
import me.googas.bot.api.events.queue.QueuePreJoinEvent;
import me.googas.bot.core.Guido;
import me.googas.bot.core.types.GuidoMatch;
import me.googas.commons.Validate;

/** An implementation for queue */
public class GuidoQueue implements Queue {

  /** The id of the guild */
  private final long guildId;

  /** The ladder of this queue */
  private final String ladder;

  /** The waiting users in the queue */
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
        Guido.getDataLoader().getGuildDataOrCreate(this.guildId).getLadder(this.ladder),
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
  public boolean join(@NonNull LinkableInfo data) {
    if (!this.isWaiting(data) && new QueuePreJoinEvent(this, data).callAndGet()) {
      this.getWaiting().add(data);
      new QueueJoinEvent(this, data).call();
      return true;
    }
    Linkable link = data.getLink();
    if (link != null) {
      link.sendMessage("You're already waiting in this queue");
    }
    return false;
  }

  @Override
  public boolean leave(@NonNull LinkableInfo data) {
    if (this.isWaiting(data)) {
      return this.getWaiting().remove(data);
    }
    return true;
  }

  @Override
  public GuidoMatch checkReady() {
    return null;
  }

  @NonNull
  public List<Queueable> getWaiting() {
    return this.waiting;
  }

  @Override
  public String toString() {
    return "GuidoQueue{"
        + "guildId="
        + this.guildId
        + ", ladder='"
        + this.ladder
        + '\''
        + ", waiting="
        + this.waiting
        + '}';
  }
}
