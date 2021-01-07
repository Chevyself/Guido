package me.googas.bot.api.types.discord;

import me.googas.api.permissions.Permissible;
import me.googas.bot.api.types.BotCatchable;

/** A role data for the bot */
public interface BotRole extends Permissible, BotCatchable {

  /**
   * Get the unique id of the role. This is an object in discord that must have its unique id
   *
   * @return the unique id of the role
   */
  long getId();
}
