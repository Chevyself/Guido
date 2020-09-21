package com.starfishst.bot.api.events.data.role;

import com.starfishst.bot.api.data.BotRole;
import org.jetbrains.annotations.NotNull;

/** Called when the data of a role gets loaded */
public class BotRoleLoadedEvent extends BotRoleEvent {
  /**
   * Create the event
   *
   * @param data the role data involved in the event
   */
  public BotRoleLoadedEvent(@NotNull BotRole data) {
    super(data);
  }
}
