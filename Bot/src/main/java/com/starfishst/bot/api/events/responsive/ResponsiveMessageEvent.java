package com.starfishst.bot.api.events.responsive;

import com.starfishst.bot.api.events.GuidoEvent;
import com.starfishst.jda.utils.responsive.ResponsiveMessage;
import org.jetbrains.annotations.NotNull;

/** An event that involves a responsive message */
public class ResponsiveMessageEvent implements GuidoEvent {

  /** The message involved in the event */
  @NotNull private final ResponsiveMessage message;

  /**
   * Create the event
   *
   * @param message the responsive message involved in the event
   */
  public ResponsiveMessageEvent(@NotNull ResponsiveMessage message) {
    this.message = message;
  }

  /**
   * Get the responsive message involved in the event
   *
   * @return the responsive message involved in the event
   */
  @NotNull
  public ResponsiveMessage getMessage() {
    return message;
  }
}
