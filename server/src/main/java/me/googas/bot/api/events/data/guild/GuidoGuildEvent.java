package me.googas.bot.api.events.data.guild;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.events.GuidoEvent;
import me.googas.bot.core.discord.GuidoGuild;
import me.googas.commons.builder.ToStringBuilder;

/** This object represents an event which has {@link GuidoGuild} involved */
public class GuidoGuildEvent implements GuidoEvent {

  @NonNull @Getter private final GuidoGuild data;

  /**
   * Create the event
   *
   * @param data the guild data that has been loaded
   */
  public GuidoGuildEvent(@NonNull GuidoGuild data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("data", this.data).build();
  }
}
