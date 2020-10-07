package com.starfishst.bot.api.events.data.unlinked;

import com.starfishst.bot.api.data.BotUnlinkedMemberData;
import org.jetbrains.annotations.NotNull;

/** Called when an unlinked member got unloaded */
public class BotUnlinkedMemberUnloadedEvent extends BotUnlinkedMemberEvent {
  /**
   * Create the event
   *
   * @param member the member involved in the event
   */
  public BotUnlinkedMemberUnloadedEvent(@NotNull BotUnlinkedMemberData member) {
    super(member);
  }
}
