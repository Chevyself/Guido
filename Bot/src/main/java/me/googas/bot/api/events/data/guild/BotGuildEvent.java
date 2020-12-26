package me.googas.bot.api.events.data.guild;

import lombok.Getter;
import lombok.NonNull;
import me.googas.bot.api.events.GuidoEvent;
import me.googas.bot.api.types.discord.BotGuild;
import me.googas.commons.builder.ToStringBuilder;

/** This object represents an event which has {@link BotGuild} involved */
public class BotGuildEvent implements GuidoEvent {

  @NonNull @Getter private final BotGuild data;

  /**
   * Create the event
   *
   * @param data the guild data that has been loaded
   */
  public BotGuildEvent(@NonNull BotGuild data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("data", this.data).build();
  }
}
