package me.googas.bot.api.types.messages;

import com.starfishst.jda.utils.responsive.ResponsiveMessage;
import lombok.NonNull;

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
