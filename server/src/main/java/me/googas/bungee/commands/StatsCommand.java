package me.googas.bungee.commands;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.annotations.Free;
import com.github.chevyself.starbox.annotations.Required;
import com.github.chevyself.starbox.bungee.utils.BungeeUtils;
import com.github.chevyself.starbox.common.Async;
import com.github.chevyself.starbox.common.CommandPermission;
import com.github.chevyself.starbox.result.Result;
import me.googas.api.Requests;
import me.googas.bungee.data.ProxiedOfflinePlayer;
import me.googas.net.sockets.json.client.JsonClient;
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
  @Async
  @CommandPermission("guido.stats")
  @Command(aliases = {"globalStats", "gStats"})
  public Result stats(
      CommandSender sender,
      JsonClient client,
      @Free(name = "player", description = "The player to see the stats from")
          ProxiedOfflinePlayer player) {
    final ProxiedOfflinePlayer toSee;
    if (sender.hasPermission("guido.stats.else") && player != null) {
      toSee = player;
    } else if (player == null) {
      if (sender instanceof ProxiedPlayer) {
        toSee = new ProxiedOfflinePlayer(((ProxiedPlayer) sender).getUniqueId(), sender.getName());
      } else {
        return Result.of("&cYou must be a player to use this command");
      }
    } else {
      return Result.of("&cYou are not allowed to see someone else's stats");
    }
    Requests.Links.stats(toSee.getLink())
        .send(
            client,
            Requests.ifPresentElse(
                stats -> {
                  /* TODO this is currently broken as method returns SortedStats
                  StringBuilder builder = Strings.getBuilder();
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
                                      .append(map.getId(obj))
                                      .append("\n");
                          }
                      }
                      sender.sendMessage(new TextComponent(BungeeUtils.build(builder.toString())));
                  }
                   */
                },
                () -> {
                  // TODO could not obtain stats
                }));
    return null;
  }

  @Async
  @CommandPermission("guido.stats.reset")
  @Command(aliases = "statsReset")
  public void statsReset(
      CommandSender sender,
      JsonClient client,
      @Required(name = "player", description = "The player reset the stats to")
          ProxiedOfflinePlayer player) {
    Requests.Links.resetStats(player.getLink())
        .send(
            client,
            Requests.ifPresentElse(
                reset -> {
                  String message =
                      "&cStats for &e" + player.getNickname() + "&c could not be reset";
                  if (reset) {
                    message = "&aStats for &e" + player.getNickname() + "&a have been reset";
                  }
                  sender.sendMessage(new TextComponent(BungeeUtils.format(message)));
                },
                () -> {
                  // TODO could not reset
                }));
  }
}
