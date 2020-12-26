package me.googas.bot.api.events.data.guild;

import lombok.NonNull;
import me.googas.bot.api.types.discord.BotGuild;

/** Called when the guild data is unloaded */
public class BotGuildUnloadedEvent extends BotGuildEvent {

  /**
   * Create the event
   *
   * @param data the guild data that has been loaded
   */
  public BotGuildUnloadedEvent(@NonNull BotGuild data) {
    super(data);
  }
}
