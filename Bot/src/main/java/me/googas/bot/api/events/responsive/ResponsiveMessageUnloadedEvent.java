package me.googas.bot.api.events.responsive;

import com.starfishst.jda.utils.responsive.ResponsiveMessage;
import lombok.NonNull;

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
