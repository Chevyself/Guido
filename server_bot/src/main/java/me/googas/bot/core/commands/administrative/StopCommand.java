package me.googas.bot.core.commands.administrative;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.result.Result;
import me.googas.bot.api.Guido;
import me.googas.bot.core.commands.middleware.GuidoJdaPermission;

public class StopCommand {

  @GuidoJdaPermission("user:guido.admin")
  @Command(aliases = "stop", description = "stop.desc")
  public Result stop() {
    Guido.stop();
    return Result.of("Bot has been stopped");
  }
}
