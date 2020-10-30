package me.googas.bot.api.events.data.guild;

import me.googas.bot.api.data.BotGuild;
import org.jetbrains.annotations.NotNull;

/** Called when guild data has been loaded */
public class BotGuildLoadedEvent extends BotGuildEvent {

  /**
   * Create the event
   *
   * @param data the guild data that has been loaded
   */
  public BotGuildLoadedEvent(@NotNull BotGuild data) {
    super(data);
  }
}
