package com.starfishst.bot.api.events.data;

import com.starfishst.bot.api.data.BotUser;
import org.jetbrains.annotations.NotNull;

/** Called when the user data gets unloaded */
public class BotUserUnloadedEvent extends BotUserEvent {
  /**
   * Create the event
   *
   * @param data the user data involved in the event
   */
  public BotUserUnloadedEvent(@NotNull BotUser data) {
    super(data);
  }
}
