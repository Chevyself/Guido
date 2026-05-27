package com.starfishst.bukkit.commands;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.common.Async;
import com.github.chevyself.starbox.common.CommandPermission;
import com.github.chevyself.starbox.result.Result;
import com.starfishst.bukkit.Guido;
import com.starfishst.bukkit.lang.BukkitLocaleFile;
import com.starfishst.bukkit.modules.GroupsHandler;
import lombok.NonNull;
import me.googas.api.utility.Maps;
import org.bukkit.command.CommandSender;

/** Commands for configuration */
public class ConfigurationCommands implements GuidoCommand {

  @Async
  @CommandPermission("guido.config.async")
  @Command(aliases = "groups", description = "groups.desc")
  public Result groups(BukkitLocaleFile locale, CommandSender sender) {
    Guido.getModuleRegistry()
        .require(GroupsHandler.class)
        .reload(
            loaded -> {
              if (loaded != null) {
                sender.sendMessage(
                    locale.get(
                        "groups.reload", Maps.singleton("amount", String.valueOf(loaded.size()))));
              }
            });
    return null;
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
