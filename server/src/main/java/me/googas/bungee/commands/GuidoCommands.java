package me.googas.bungee.commands;

import com.starfishst.commands.bungee.annotations.Command;
import com.starfishst.commands.bungee.context.CommandContext;
import com.starfishst.commands.bungee.result.Result;
import com.starfishst.core.annotations.Parent;
import me.googas.bungee.GuidoBungee;

/** Commands for reloading guido */
public class GuidoCommands {

  @Parent
  @Command(aliases = "guido", permission = "guido.reload")
  public Result guido(CommandContext context) {
    this.config();
    this.servers(context);
    return new Result("&aEverything has been reloaded");
  }

  @Command(aliases = "config", permission = "guido.reload.config")
  public Result config() {
    GuidoBungee.validated().loadConfiguration();
    return new Result("&aConfiguration has been reloaded");
  }

  @Command(aliases = "server", permission = "guido.reload.server")
  public Result servers(CommandContext context) {
    if (context.hasFlag("-c")) {
      this.config();
      return new Result("&aConfiguration and servers have been reloaded");
    }
    GuidoBungee.validated().loadServers();
    return new Result("&aServers have been reloaded");
  }
}
