package com.starfishst.bot.api.events.data.user;

import com.starfishst.bot.api.data.BotUser;
import com.starfishst.bot.api.events.GuidoEvent;
import org.jetbrains.annotations.NotNull;

/** An event that involves user data */
public class BotUserEvent implements GuidoEvent {

  /** The data involved in the event */
  @NotNull private final BotUser data;

  /**
   * Create the event
   *
   * @param data the user data involved in the event
   */
  public BotUserEvent(@NotNull BotUser data) {
    this.data = data;
  }

  /**
   * Get the user data involved in the event
   *
   * @return the user data involved in the event
   */
  @NotNull
  public BotUser getData() {
    return this.data;
  }
}
