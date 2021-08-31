package me.googas.guido.receptors;

import java.util.UUID;
import me.googas.net.sockets.json.ParamName;
import me.googas.net.sockets.json.Receptor;
import me.googas.reflect.wrappers.chat.Component;
import me.googas.reflect.wrappers.chat.WrappedHoverEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class LinkReceptors {

  @Receptor("link/notify")
  public UUID notify(
      @ParamName("username") String username,
      @ParamName("code") int code,
      @ParamName("tag") String tag) {
    Player onlinePlayer =
        Bukkit.getOnlinePlayers().stream()
            .filter(player -> player.getName().equalsIgnoreCase(username))
            .findFirst()
            .orElse(null);
    if (onlinePlayer != null) {
      onlinePlayer
          .spigot()
          .sendMessage(
              new Component()
                  .append("Are you linking your account with ")
                  .color(ChatColor.DARK_GREEN)
                  .append(tag)
                  .color(ChatColor.GREEN)
                  .append("?")
                  .color(ChatColor.GREEN)
                  .append(" [")
                  .color(ChatColor.DARK_GRAY)
                  .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/link " + code))
                  .event(
                      WrappedHoverEvent.construct(
                          HoverEvent.Action.SHOW_TEXT,
                          new Component().append("Link account").color(ChatColor.AQUA).build()))
                  .append("Link")
                  .color(ChatColor.GREEN)
                  .append("]")
                  .color(ChatColor.DARK_GRAY)
                  .build());
      return onlinePlayer.getUniqueId();
    }
    return null;
  }
}
