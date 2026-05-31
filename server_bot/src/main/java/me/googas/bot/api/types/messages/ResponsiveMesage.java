package me.googas.bot.api.types.messages;

import lombok.NonNull;
import me.googas.starbox.jda.responsive.ResponsiveMessage;

/** An extension for responsive messages */
public interface ResponsiveMesage extends ResponsiveMessage {

  /**
   * The type of responsive message
   *
   * @return the type of responsive message
   */
  @NonNull
  String getType();
}
