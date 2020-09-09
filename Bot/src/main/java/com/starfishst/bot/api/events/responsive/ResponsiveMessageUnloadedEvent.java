package com.starfishst.bot.api.events.responsive;

import com.starfishst.commands.utils.responsive.ResponsiveMessage;
import org.jetbrains.annotations.NotNull;

/** Called when a responsive message gets unloaded */
public class ResponsiveMessageUnloadedEvent extends ResponsiveMessageEvent {
  /**
   * Create the event
   *
   * @param message the responsive message involved in the event
   */
  public ResponsiveMessageUnloadedEvent(@NotNull ResponsiveMessage message) {
    super(message);
  }
}
