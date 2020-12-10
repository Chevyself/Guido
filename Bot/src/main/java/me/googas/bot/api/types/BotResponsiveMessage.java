package me.googas.bot.api.types;

import com.starfishst.jda.utils.responsive.ResponsiveMessage;
import lombok.NonNull;

/** An extension for responsive messages */
public interface BotResponsiveMessage extends ResponsiveMessage {

  /**
   * The type of responsive message
   *
   * @return the type of responsive message
   */
  @NonNull
  String getType();
}
