package me.googas.bot.core.handlers.responsive.lang;

import com.starfishst.jda.utils.responsive.ReactionResponse;
import com.starfishst.jda.utils.responsive.ResponsiveMessage;
import me.googas.bot.api.types.BotLinkedData;
import me.googas.bot.core.Guido;
import me.googas.bot.core.handlers.responsive.GuidoMessagesController;
import me.googas.bot.core.util.console.Console;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

/** The response to change the language from an user */
public class LangChangeReactionResponse implements ReactionResponse {

  /** The id of the user to change */
  private final long userId;

  /** The unicode to use. This will also be used to get the language */
  @NotNull private final String unicode;

  /**
   * Create the reaction response
   *
   * @param userId the id of the user to change
   * @param unicode the unicode to use. Also used to get the language
   */
  public LangChangeReactionResponse(long userId, @NotNull String unicode) {
    this.userId = userId;
    this.unicode = unicode;
  }

  @Override
  public boolean removeReaction() {
    return false;
  }

  @Override
  public void onReaction(@NotNull MessageReactionAddEvent event) {
    if (event.getUserIdLong() == this.userId) {
      String lang = Guido.getLanguageHandler().getFileFromUnicode(this.unicode).getLang();
      BotLinkedData userData = Guido.getDataLoader().getDiscordUserData(event.getUserIdLong());
      Console.debug("Changing the language for " + userData + " to " + lang);
      userData.getPreferences().addValue("lang", lang);
      ResponsiveMessage responsiveMessage =
          Guido.getHandler(GuidoMessagesController.class)
              .getResponsiveMessage(null, event.getMessageIdLong());
      if (responsiveMessage instanceof LangChangeResponsiveMessage) {
        try {
          ((LangChangeResponsiveMessage) responsiveMessage).unload(true);
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }
      }
    }
  }

  @Override
  public @NotNull String getUnicode() {
    return this.unicode;
  }
}
