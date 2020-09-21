package com.starfishst.bot.api.events.data.guild;

import com.starfishst.bot.api.data.BotGuild;
import org.jetbrains.annotations.NotNull;

/** Called when the guild data is unloaded */
public class BotGuildUnloadedEvent extends BotGuildEvent {

  /**
   * Create the event
   *
   * @param data the guild data that has been loaded
   */
  public BotGuildUnloadedEvent(@NotNull BotGuild data) {
    super(data);
  }
}
