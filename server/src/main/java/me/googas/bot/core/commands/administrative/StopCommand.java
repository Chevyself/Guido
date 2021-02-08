package me.googas.bot.core.commands.administrative;

import com.starfishst.commands.jda.annotations.Command;
import com.starfishst.commands.jda.result.Result;
import me.googas.bot.api.Guido;

public class StopCommand {

  @Command(aliases = "stop", description = "stop.desc", node = "user:guido.admin")
  public Result stop() {
    Guido.stop();
    return new Result("Bot has been stopped");
  }
}
