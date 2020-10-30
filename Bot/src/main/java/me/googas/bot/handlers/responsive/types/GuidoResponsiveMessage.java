package me.googas.bot.handlers.responsive.types;

import com.starfishst.jda.utils.responsive.ResponsiveMessage;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

/** A responsive message made for the guido bot */
public interface GuidoResponsiveMessage extends ResponsiveMessage {

  /**
   * Get the guild id where this message is from
   *
   * @return the id of the guild or -1 if it is not from one
   */
  long guildId();

  /**
   * Saves the responsive message
   *
   * @param jda the jda instance to make changes to the message
   */
  void save(@NotNull JDA jda);
}
