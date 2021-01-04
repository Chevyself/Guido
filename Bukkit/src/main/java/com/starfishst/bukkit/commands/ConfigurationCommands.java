package com.starfishst.bukkit.commands;

import com.starfishst.bukkit.annotations.Command;
import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.commands.GuidoCommand;
import com.starfishst.bukkit.lang.BukkitLocaleFile;
import com.starfishst.bukkit.listeners.GroupsHandler;
import com.starfishst.bukkit.result.Result;
import com.starfishst.core.annotations.Settings;
import lombok.NonNull;
import me.googas.commons.maps.Maps;
import org.bukkit.command.CommandSender;

/** Commands for configuration */
public class ConfigurationCommands implements GuidoCommand {

  @Settings("async")
  @Command(aliases = "groups", description = "groups.desc", permission = "guido.config.groups")
  public Result groups(BukkitLocaleFile locale, CommandSender sender) {
    Guido.getHandlerRegistry()
        .requireHandler(GroupsHandler.class)
        .reload(
            loaded -> {
              if (loaded != null) {
                sender.sendMessage(
                    locale.get(
                        "groups.reload", Maps.singleton("amount", String.valueOf(loaded.size()))));
              }
            });
    return new Result();
  }

  @Override
  public void setEnabled(boolean bol) {}

  @Override
  public @NonNull String getName() {
    return "configuration";
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
