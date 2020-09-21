package com.starfishst.bot.api.events.data.user;

import com.starfishst.bot.api.data.BotUser;
import org.jetbrains.annotations.NotNull;

/** Called when the user data gets loaded */
public class BotUserLoadedEvent extends BotUserEvent {
  /**
   * Create the event
   *
   * @param data the user data involved in the event
   */
  public BotUserLoadedEvent(@NotNull BotUser data) {
    super(data);
  }
}
