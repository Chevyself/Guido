package com.starfishst.bot.api.events.data.unlinked;

import com.starfishst.bot.api.data.BotUnlinkedMember;
import org.jetbrains.annotations.NotNull;

/** Called when an unlinked member got unloaded */
public class BotUnlinkedMemberUnloadedEvent extends BotUnlinkedMemberEvent {
  /**
   * Create the event
   *
   * @param member the member involved in the event
   */
  public BotUnlinkedMemberUnloadedEvent(@NotNull BotUnlinkedMember member) {
    super(member);
  }
}
