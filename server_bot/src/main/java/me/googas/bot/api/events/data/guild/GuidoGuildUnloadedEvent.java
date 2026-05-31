package me.googas.bot.api.events.data.guild;

import lombok.NonNull;
import me.googas.bot.core.discord.GuidoGuild;

/** Called when the guild data is unloaded */
public class GuidoGuildUnloadedEvent extends GuidoGuildEvent {

  /**
   * Create the event
   *
   * @param data the guild data that has been loaded
   */
  public GuidoGuildUnloadedEvent(@NonNull GuidoGuild data) {
    super(data);
  }
}
