package com.starfishst.bungee.core.commands;

import com.starfishst.bungee.annotations.Command;
import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.context.CommandContext;
import com.starfishst.bungee.result.Result;
import com.starfishst.core.annotations.Parent;

/** Commands for reloading guido */
public class GuidoCommands {

  /**
   * This command reloads everything in guido bungee
   *
   * @param context the context of the command
   * @return a result saying that everything was reloaded
   */
  @Parent
  @Command(aliases = "guido", permission = "guido.reload")
  public Result guido(CommandContext context) {
    this.config();
    this.servers(context);
    return new Result("&aEverything has been reloaded");
  }

  /**
   * This command reloads the config only
   *
   * @return a result saying that config was reloaded
   */
  @Command(aliases = "config", permission = "guido.reload.config")
  public Result config() {
    Guido.validated().loadConfiguration();
    return new Result("&aConfiguration has been reloaded");
  }

  /**
   * This command reloads the server only
   *
   * @param context the context to get if the person used the flag '-c' to reload the config too
   * @return a result to say that servers were reloaded
   */
  @Command(aliases = "server", permission = "guido.reload.server")
  public Result servers(CommandContext context) {
    if (context.hasFlag("-c")) {
      this.config();
      return new Result("&aConfiguration and servers have been reloaded");
    }
    Guido.validated().loadServers();
    return new Result("&aServers have been reloaded");
  }
}
