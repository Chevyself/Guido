package com.starfishst.bungee.core.commands;

import com.starfishst.bungee.annotations.Command;
import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.context.CommandContext;
import com.starfishst.bungee.core.lang.BungeeLocaleFile;
import com.starfishst.bungee.core.listeners.GroupListener;
import com.starfishst.bungee.result.Result;
import com.starfishst.core.annotations.Parent;
import com.starfishst.core.annotations.Settings;
import me.googas.commons.maps.Maps;
import net.md_5.bungee.api.CommandSender;

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
    Guido.validated().loadConfiguration();
    return new Result("&aConfiguration has been reloaded");
  }

  @Command(aliases = "server", permission = "guido.reload.server")
  public Result servers(CommandContext context) {
    if (context.hasFlag("-c")) {
      this.config();
      return new Result("&aConfiguration and servers have been reloaded");
    }
    Guido.validated().loadServers();
    return new Result("&aServers have been reloaded");
  }

  @Settings("async")
  @Command(
      aliases = {"groups"},
      permission = "guido.reload.groups")
  public void reload(BungeeLocaleFile locale, CommandSender sender) {
    Guido.getListener(GroupListener.class)
        .reload(
            loaded -> {
              if (loaded != null) {
                sender.sendMessage(
                    locale.getComponent(
                        "groups.reload", Maps.singleton("amount", String.valueOf(loaded.size()))));
              }
            });
  }
}
