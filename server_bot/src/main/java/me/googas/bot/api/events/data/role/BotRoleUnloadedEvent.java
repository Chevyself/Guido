package me.googas.bot.api.events.data.role;

import lombok.NonNull;
import me.googas.bot.core.discord.GuidoRole;

/** Called when the data of a role gets unloaded */
public class BotRoleUnloadedEvent extends BotRoleEvent {
  /**
   * Create the event
   *
   * @param data the role data involved in the event
   */
  public BotRoleUnloadedEvent(@NonNull GuidoRole data) {
    super(data);
  }
}
