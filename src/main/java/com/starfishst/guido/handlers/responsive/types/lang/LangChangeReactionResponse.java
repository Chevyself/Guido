package com.starfishst.guido.handlers.responsive.types.lang;

import com.starfishst.commands.utils.responsive.ReactionResponse;
import com.starfishst.commands.utils.responsive.ResponsiveMessage;
import com.starfishst.guido.Guido;
import com.starfishst.guido.api.data.UserData;
import com.starfishst.guido.handlers.responsive.GuidoMessagesController;
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
      UserData userData = Guido.getDataLoader().getUserData(event.getUserIdLong());
      userData.setLang(Guido.getLanguageHandler().getFileFromUnicode(this.unicode).getLang());
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
