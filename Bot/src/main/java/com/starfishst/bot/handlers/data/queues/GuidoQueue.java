package com.starfishst.bot.handlers.data.queues;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.events.queue.QueueJoinEvent;
import com.starfishst.bot.handlers.data.GuidoMatch;
import com.starfishst.guido.api.data.links.LinkedInfo;
import com.starfishst.guido.api.data.matches.Ladder;
import com.starfishst.guido.api.data.matches.Queue;
import java.util.ArrayList;
import java.util.List;
import me.googas.commons.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** An implementation for queue */
public class GuidoQueue implements Queue {

  /** The id of the guild */
  private final long guildId;

  /** The ladder of this queue */
  private final String ladder;

  /** The waiting users in the queue */
  private final List<LinkedInfo> waiting = new ArrayList<>();

  /**
   * Create the queue
   *
   * @param guildId the id where this queue was created
   * @param ladder the names of the ladders waiting for queue
   */
  public GuidoQueue(long guildId, @NotNull String ladder) {
    this.guildId = guildId;
    this.ladder = ladder;
  }

  /**
   * Get the ladder where this queue is happening
   *
   * @return the ladder
   */
  @NotNull
  public Ladder getLadder() {
    return Validate.notNull(
        Guido.getDataLoader().getGuildData(this.guildId).getLadder(this.ladder),
        "Ladder was deleted?");
  }

  @NotNull
  public String getLadderName() {
    return this.ladder;
  }

  @Override
  public long getGuildId() {
    return this.guildId;
  }

  @Override
  public boolean join(@NotNull LinkedInfo data) {
    if (!this.isWaiting(data)) {
      this.getWaiting().add(data);
      new QueueJoinEvent(this, data).call();
      return true;
    }
    return false;
  }

  @Override
  public boolean leave(@NotNull LinkedInfo data) {
    if (this.isWaiting(data)) {
      return this.getWaiting().remove(data);
    }
    return true;
  }

  @Override
  @Nullable
  public GuidoMatch checkReady() {
    return null;
  }

  @NotNull
  public List<LinkedInfo> getWaiting() {
    return this.waiting;
  }
}
