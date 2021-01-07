package me.googas.bot.core.handlers.responsive.lang;

import com.starfishst.jda.utils.responsive.ReactionResponse;
import com.starfishst.jda.utils.responsive.ResponsiveMessage;
import lombok.NonNull;
import me.googas.api.links.ref.DiscordLinkable;
import me.googas.bot.api.Guido;
import me.googas.bot.core.handlers.responsive.GuidoMessagesController;
import me.googas.bot.core.util.Discord;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

/** The response to change the language from an user */
public class LangChangeReactionResponse implements ReactionResponse {

  /** The id of the user to change */
  private final long userId;

  /** The unicode to use. This will also be used to get the language */
  @NonNull private final String unicode;

  /**
   * Create the reaction response
   *
   * @param userId the id of the user to change
   * @param unicode the unicode to use. Also used to get the language
   */
  public LangChangeReactionResponse(long userId, @NonNull String unicode) {
    this.userId = userId;
    this.unicode = unicode;
  }

  @Override
  public boolean removeReaction() {
    return false;
  }

  @Override
  public boolean onReaction(@NonNull MessageReactionAddEvent event) {
    if (event.getUserIdLong() == this.userId) {
      String lang =
          Guido.getHandlers().getLanguageHandler().getFileFromUnicode(this.unicode).getLang();
      DiscordLinkable userData = Discord.getUser(event.getUserIdLong());
      userData.getPreferences().put("lang", lang);
      ResponsiveMessage responsiveMessage =
          Guido.getHandlers()
              .getHandler(GuidoMessagesController.class)
              .getResponsiveMessage(null, event.getMessageIdLong());
      if (responsiveMessage instanceof LangChangeResponsiveMessage) {
        try {
          ((LangChangeResponsiveMessage) responsiveMessage).unload(true);
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }
      }
    }
    return true;
  }

  @Override
  public @NonNull String getUnicode() {
    return this.unicode;
  }
}
