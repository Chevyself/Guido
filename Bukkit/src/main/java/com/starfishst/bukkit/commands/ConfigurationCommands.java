package com.starfishst.bukkit.commands;

import com.starfishst.bukkit.annotations.Command;
import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.lang.BukkitLocaleFile;
import com.starfishst.bukkit.listeners.GroupListener;
import com.starfishst.bukkit.result.Result;
import com.starfishst.core.annotations.settings.Setting;
import com.starfishst.core.annotations.settings.Settings;
import me.googas.commons.maps.Maps;
import org.bukkit.command.CommandSender;

/** Commands for configuration */
public class ConfigurationCommands {

  /**
   * Reload the loaded groups
   *
   * @param locale the locale of the sender
   * @param sender the sender of the command
   * @return empty result
   */
  @Settings(settings = @Setting(key = "async", value = "true"))
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
}
