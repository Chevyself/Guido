package com.starfishst.bot.commands;

import com.starfishst.bot.Guido;
import com.starfishst.bot.handlers.lang.GuidoLanguageHandler;
import com.starfishst.bot.handlers.responsive.GuidoMessagesController;
import com.starfishst.bot.handlers.responsive.types.lang.LangChangeResponsiveMessage;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.result.Result;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

/** Commands to change the language for a user */
public class LangCommands {

  /** The handler to localize the messages of the command */
  @NotNull private final GuidoLanguageHandler handler = Guido.getLanguageHandler();

  /**
   * Change the language of the user
   *
   * @param context the context of the command
   * @param user the user to change the language
   * @return the message to change the language
   */
  @Command(aliases = "lang", description = "lang.desc")
  public Result lang(CommandContext context, User user) {
    return new Result(
        this.handler.getFile(context).get("lang.change"),
        msg -> {
          LangChangeResponsiveMessage responsiveMessage =
              new LangChangeResponsiveMessage(user, msg);
          Guido.getHandler(GuidoMessagesController.class).addMessage(responsiveMessage);
        });
  }
}
