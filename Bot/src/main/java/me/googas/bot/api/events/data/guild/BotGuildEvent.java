package me.googas.bot.api.events.data.guild;

import lombok.NonNull;
import me.googas.bot.api.events.GuidoEvent;
import me.googas.bot.api.types.BotGuild;

/** This object represents an event which has {@link BotGuild} involved */
public class BotGuildEvent implements GuidoEvent {

  /** The guild data that was involved in the event */
  @NonNull private final BotGuild data;

  /**
   * Create the event
   *
   * @param data the guild data that has been loaded
   */
  public BotGuildEvent(@NonNull BotGuild data) {
    this.data = data;
  }

  /**
   * Get the guild data that was involved in the event
   *
   * @return the data that was involved in the event
   */
  @NonNull
  public BotGuild getData() {
    return this.data;
  }

  @Override
  public String toString() {
    return "BotGuildEvent{" + "data=" + this.data + '}';
  }
}
