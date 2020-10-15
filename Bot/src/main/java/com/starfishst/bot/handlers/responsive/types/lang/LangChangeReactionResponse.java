package com.starfishst.bot.handlers.responsive.types.lang;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.loader.BotLinkedData;
import com.starfishst.bot.handlers.responsive.GuidoMessagesController;
import com.starfishst.jda.utils.responsive.ReactionResponse;
import com.starfishst.jda.utils.responsive.ResponsiveMessage;
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
      BotLinkedData userData = Guido.getDataLoader().getDiscordUserData(event.getUserIdLong());
      userData
          .getPreferences()
          .addValue("lang", Guido.getLanguageHandler().getFileFromUnicode(this.unicode).getLang());
      ResponsiveMessage responsiveMessage =
          Guido.getHandler(GuidoMessagesController.class)
              .getResponsiveMessage(null, event.getMessageIdLong());
      if (responsiveMessage instanceof LangChangeResponsiveMessage) {
        ((LangChangeResponsiveMessage) responsiveMessage).unload();
        ((LangChangeResponsiveMessage) responsiveMessage).onRemove();
      }
    }
  }

  @Override
  public @NotNull String getUnicode() {
    return this.unicode;
  }
}
