package com.starfishst.bot.api.events.data;

import com.starfishst.bot.api.data.BotMember;
import org.jetbrains.annotations.NotNull;

/** Called when the data from a member gets unloaded */
public class BotMemberUnloadedEvent extends BotMemberEvent {
  /**
   * Create the event
   *
   * @param data the data involved in the event
   */
  public BotMemberUnloadedEvent(@NotNull BotMember data) {
    super(data);
  }
}
