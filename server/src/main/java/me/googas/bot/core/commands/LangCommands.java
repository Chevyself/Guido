package me.googas.bot.core.commands;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.result.Result;
import lombok.NonNull;
import me.googas.bot.api.Guido;
import me.googas.bot.core.handlers.responsive.GuidoMessagesController;
import me.googas.bot.core.handlers.responsive.lang.LangChangeResponsiveMessage;
import me.googas.bot.core.lang.GuidoLanguageHandler;
import net.dv8tion.jda.api.entities.User;

/** Commands to change the language for a user */
public class LangCommands {

  /** The handler to localize the messages of the command */
  @NonNull private final GuidoLanguageHandler handler = Guido.getHandlers().getLanguageHandler();

  @Command(aliases = "lang", description = "lang.desc")
  public Result lang(CommandContext context, User user) {
    return new Result(
        this.handler.getFile(context).get("lang.change"),
        msg -> {
          LangChangeResponsiveMessage responsiveMessage =
              new LangChangeResponsiveMessage(user, msg);
          Guido.getHandlers()
              .getHandler(GuidoMessagesController.class)
              .addMessage(responsiveMessage);
        });
  }
}
