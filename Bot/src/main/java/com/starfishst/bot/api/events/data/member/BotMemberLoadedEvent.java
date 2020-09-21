package com.starfishst.bot.api.events.data.member;

import com.starfishst.bot.api.data.BotMember;
import org.jetbrains.annotations.NotNull;

/** Called when the data of a member gets loaded */
public class BotMemberLoadedEvent extends BotMemberEvent {
  /**
   * Create the event
   *
   * @param data the data involved in the event
   */
  public BotMemberLoadedEvent(@NotNull BotMember data) {
    super(data);
  }
}
