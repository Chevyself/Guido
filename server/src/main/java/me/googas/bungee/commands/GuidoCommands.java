package me.googas.bungee.commands;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.annotations.Parent;
import com.github.chevyself.starbox.bungee.context.CommandContext;
import com.github.chevyself.starbox.result.Result;
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
