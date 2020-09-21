package com.starfishst.bot.api.events.data.member;

import com.starfishst.bot.api.data.BotMember;
import com.starfishst.bot.api.events.GuidoEvent;
import org.jetbrains.annotations.NotNull;

/** An event that involves the data from a member {@link BotMember} */
public class BotMemberEvent implements GuidoEvent {

  /** The data of the member involved */
  @NotNull private final BotMember data;

  /**
   * Create the event
   *
   * @param data the data involved in the event
   */
  public BotMemberEvent(@NotNull BotMember data) {
    this.data = data;
  }

  /**
   * Get the member data involved in the event
   *
   * @return the member data involved in the event
   */
  @NotNull
  public BotMember getData() {
    return data;
  }
}
