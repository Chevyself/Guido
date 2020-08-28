package com.starfishst.guido.commands;

import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.context.CommandContext;
import com.starfishst.commands.result.Result;
import com.starfishst.guido.Guido;
import com.starfishst.guido.handlers.responsive.GuidoMessagesController;
import com.starfishst.guido.handlers.responsive.types.lang.LangChangeResponsiveMessage;
import com.starfishst.guido.lang.GuidoLanguageHandler;
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
  @Command(aliases = "lang", description = "cmd.lang.desc")
  public Result lang(CommandContext context, User user) {
    return new Result(
        handler.getFile(context).get("cmd.lang"),
        msg -> {
          LangChangeResponsiveMessage responsiveMessage =
              new LangChangeResponsiveMessage(user, msg);
          Guido.getHandler(GuidoMessagesController.class).addMessage(responsiveMessage);
        });
  }
}
