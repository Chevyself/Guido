package me.googas.bungee.commands;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.annotations.Parent;
import com.github.chevyself.starbox.bungee.context.CommandContext;
import com.github.chevyself.starbox.common.CommandPermission;
import com.github.chevyself.starbox.result.Result;
import me.googas.bungee.GuidoBungee;

/** Commands for reloading guido */
public class GuidoCommands {

  @Parent
  @CommandPermission("guido.reload")
  @Command(aliases = "guido")
  public Result guido(CommandContext context) {
    this.config();
    this.servers(context);
    return Result.of("&aEverything has been reloaded");
  }

  @Command(aliases = "config")
  public Result config() {
    GuidoBungee.validated().loadConfiguration();
    return Result.of("&aConfiguration has been reloaded");
  }

  @Command(aliases = "server")
  public Result servers(CommandContext context) {
    if (context.hasFlag("-c")) {
      this.config();
      return Result.of("&aConfiguration and servers have been reloaded");
    }
    GuidoBungee.validated().loadServers();
    return Result.of("&aServers have been reloaded");
  }
}
