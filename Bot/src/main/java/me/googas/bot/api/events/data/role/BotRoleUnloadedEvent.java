package me.googas.bot.api.events.data.role;

import me.googas.bot.api.types.BotRole;
import org.jetbrains.annotations.NotNull;

/** Called when the data of a role gets unloaded */
public class BotRoleUnloadedEvent extends BotRoleEvent {
  /**
   * Create the event
   *
   * @param data the role data involved in the event
   */
  public BotRoleUnloadedEvent(@NotNull BotRole data) {
    super(data);
  }
}
