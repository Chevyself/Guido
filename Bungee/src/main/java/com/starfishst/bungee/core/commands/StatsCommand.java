package com.starfishst.bungee.core.commands;

import com.starfishst.bungee.annotations.Command;
import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.core.data.ProxiedOfflinePlayer;
import com.starfishst.bungee.result.Result;
import com.starfishst.bungee.utils.BungeeUtils;
import com.starfishst.core.annotations.Optional;
import com.starfishst.core.annotations.Required;
import com.starfishst.core.annotations.settings.Setting;
import com.starfishst.core.annotations.settings.Settings;
import java.util.Map;
import me.googas.commons.Strings;
import me.googas.messaging.Request;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/** Commands related to stats */
public class StatsCommand {

  /**
   * See the stats for a player
   *
   * @param sender the sender of the command
   * @param player the player to see the stats
   * @return the stats of the command
   */
  @Settings(settings = @Setting(key = "async", value = "true"))
  @Command(
      aliases = {"globalStats"},
      permission = "guido.stats")
  public Result stats(
      CommandSender sender,
      @Optional(name = "player", description = "The player to see the stats from")
          ProxiedOfflinePlayer player) {
    final ProxiedOfflinePlayer toSee;
    if (sender.hasPermission("guido.stats.else") && player != null) {
      toSee = player;
    } else if (player == null) {
      if (sender instanceof ProxiedPlayer) {
        toSee = new ProxiedOfflinePlayer(((ProxiedPlayer) sender).getUniqueId(), sender.getName());
      } else {
        return new Result("&cYou must be a player to use this command");
      }
    } else {
      return new Result("&cYou are not allowed to see someone else's stats");
    }
    Guido.getClient()
        .request(
            new Request<>(Map.class, "stats", toSee.getParams()),
            map -> {
              StringBuilder builder = Strings.getBuilder();
              if (map != null) {
                if (map.keySet().isEmpty()) {
                  sender.sendMessage(
                      new TextComponent(
                          BungeeUtils.build(
                              "&e" + toSee.getNickname() + "&a does not have stats yet")));
                } else {
                  builder.append("&7Seeing stats for &a").append(toSee.getNickname()).append("\n");
                  for (Object obj : map.keySet()) {
                    if (obj instanceof String) {
                      builder
                          .append("&8- &a")
                          .append(obj)
                          .append(": &e")
                          .append(map.get(obj))
                          .append("\n");
                    }
                  }
                  sender.sendMessage(new TextComponent(BungeeUtils.build(builder.toString())));
                }
              } else {
                sender.sendMessage(new TextComponent(BungeeUtils.build("&cStats not found!")));
              }
            });
    return new Result();
  }

  /**
   * Reset the stats for a player
   *
   * @param sender the sender of the command
   * @param player the player to reset the stats to
   * @return the result of the command
   */
  @Settings(settings = @Setting(key = "async", value = "true"))
  @Command(aliases = "statsReset", permission = "guido.stats.reset")
  public Result statsReset(
      CommandSender sender,
      @Required(name = "player", description = "The player reset the stats to")
          ProxiedOfflinePlayer player) {
    Guido.getClient()
        .request(
            new Request<>(Boolean.class, "reset-stats", player.getParams()),
            bol -> {
              String message;
              if (bol) {
                message = "&aStats for &e" + player.getNickname() + "&a have been reset";
              } else {
                message = "&cStats for &e" + player.getNickname() + "&c could not be reset";
              }
              sender.sendMessage(new TextComponent(BungeeUtils.build(message)));
            });
    return new Result();
  }
}
