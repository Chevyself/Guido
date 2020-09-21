package com.starfishst.bot.api.events.data.unlinked;

import com.starfishst.bot.api.data.BotUnlinkedMember;
import com.starfishst.bot.api.events.GuidoEvent;
import org.jetbrains.annotations.NotNull;

/** An event where a bot unlinked is involved */
public class BotUnlinkedMemberEvent implements GuidoEvent {

  /** The unlinked member involved in the event */
  @NotNull private final BotUnlinkedMember member;

  /**
   * Create the event
   *
   * @param member the member involved in the event
   */
  public BotUnlinkedMemberEvent(@NotNull BotUnlinkedMember member) {
    this.member = member;
  }

  /**
   * Get the member involved
   *
   * @return the unlinked member
   */
  @NotNull
  public BotUnlinkedMember getMember() {
    return member;
  }
}
