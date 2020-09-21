package com.starfishst.bot.api.events.data.guild;

import com.starfishst.bot.api.data.BotGuild;
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
