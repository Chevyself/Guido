package com.starfishst.bukkit.commands;

import com.starfishst.bukkit.annotations.Command;
import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.commands.GuidoCommand;
import com.starfishst.bukkit.lang.BukkitLocaleFile;
import com.starfishst.bukkit.listeners.GroupListener;
import com.starfishst.bukkit.result.Result;
import com.starfishst.core.annotations.Settings;
import lombok.NonNull;
import me.googas.commons.maps.Maps;
import org.bukkit.command.CommandSender;

/** Commands for configuration */
public class ConfigurationCommands implements GuidoCommand {

  /**
   * Reload the loaded groups
   *
   * @param locale the locale of the sender
   * @param sender the sender of the command
   * @return empty result
   */
  @Settings("async")
  @Command(aliases = "groups", description = "groups.desc", permission = "guido.config.groups")
  public Result groups(BukkitLocaleFile locale, CommandSender sender) {
    Guido.getListener(GroupListener.class)
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

  /**
   * Set whether the command is enabled
   *
   * @param bol the new value
   */
  @Override
  public void setEnabled(boolean bol) {}

  /**
   * Get the name of the command. This is used to enable it or not from the configuration
   *
   * @return the name of the command
   */
  @Override
  public @NonNull String getName() {
    return "configuration";
  }

  /**
   * Get whether the command is enabled
   *
   * @return true if the command is enabled
   */
  @Override
  public boolean isEnabled() {
    return true;
  }
}
