package com.starfishst.bot.api.events.data.unlinked;

import com.starfishst.bot.api.data.BotUnlinkedMember;
import org.jetbrains.annotations.NotNull;

/** Called when an unlinked member got loaded */
public class BotUnlinkedMemberLoadedEvent extends BotUnlinkedMemberEvent {
  /**
   * Create the event
   *
   * @param member the member involved in the event
   */
  public BotUnlinkedMemberLoadedEvent(@NotNull BotUnlinkedMember member) {
    super(member);
  }
}
