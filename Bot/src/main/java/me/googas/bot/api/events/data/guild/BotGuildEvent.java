package me.googas.bot.api.events.data.guild;

import me.googas.bot.api.events.GuidoEvent;
import me.googas.bot.api.types.BotGuild;
import org.jetbrains.annotations.NotNull;

/** This object represents an event which has {@link BotGuild} involved */
public class BotGuildEvent implements GuidoEvent {

  /** The guild data that was involved in the event */
  @NotNull private final BotGuild data;

  /**
   * Create the event
   *
   * @param data the guild data that has been loaded
   */
  public BotGuildEvent(@NotNull BotGuild data) {
    this.data = data;
  }

  /**
   * Get the guild data that was involved in the event
   *
   * @return the data that was involved in the event
   */
  @NotNull
  public BotGuild getData() {
    return this.data;
  }

  @Override
  public String toString() {
    return "BotGuildEvent{" + "data=" + this.data + '}';
  }
}
