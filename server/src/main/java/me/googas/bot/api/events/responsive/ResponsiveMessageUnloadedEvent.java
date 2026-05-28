package me.googas.bot.api.events.responsive;

import lombok.NonNull;
import me.googas.starbox.jda.responsive.ResponsiveMessage;

/** Called when a responsive message gets unloaded */
public class ResponsiveMessageUnloadedEvent extends ResponsiveMessageEvent {
  /**
   * Create the event
   *
   * @param message the responsive message involved in the event
   */
  public ResponsiveMessageUnloadedEvent(@NonNull ResponsiveMessage message) {
    super(message);
  }
}
