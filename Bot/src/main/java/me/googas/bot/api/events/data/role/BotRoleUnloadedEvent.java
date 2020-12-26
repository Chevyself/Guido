package me.googas.bot.api.events.data.role;

import lombok.NonNull;
import me.googas.bot.api.types.discord.BotRole;

/** Called when the data of a role gets unloaded */
public class BotRoleUnloadedEvent extends BotRoleEvent {
  /**
   * Create the event
   *
   * @param data the role data involved in the event
   */
  public BotRoleUnloadedEvent(@NonNull BotRole data) {
    super(data);
  }
}
