package me.googas.bot.api.events.responsive;

import com.starfishst.jda.utils.responsive.ResponsiveMessage;
import lombok.Getter;
import lombok.NonNull;
import me.googas.bot.api.events.GuidoEvent;

/** An event that involves a responsive message */
public class ResponsiveMessageEvent implements GuidoEvent {

  /** The message involved in the event */
  @NonNull @Getter private final ResponsiveMessage message;

  /**
   * Create the event
   *
   * @param message the responsive message involved in the event
   */
  public ResponsiveMessageEvent(@NonNull ResponsiveMessage message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return "ResponsiveMessageEvent{" + "message=" + this.message + '}';
  }
}
