package com.starfishst.bungee.core.commands;

import com.starfishst.bungee.annotations.Command;
import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.context.CommandContext;
import com.starfishst.bungee.result.Result;
import com.starfishst.core.annotations.Parent;

/** Commands for reloading guido */
public class GuidoCommands {

  @Parent
  @Command(aliases = "guido")
  public Result guido() {
    return new Result("Reload");
  }

  @Command(aliases = "config")
  public Result config() {
    Guido.validated().loadConfiguration();
    return new Result("Configuration has been reloaded");
  }

  @Command(aliases = "server")
  public Result servers(CommandContext context) {
    if (context.hasFlag("-c")) {
      this.config();
    }
    Guido.validated().loadServers();
    return new Result("Servers have been reloaded");
  }
}
