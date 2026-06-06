package me.googas.bungee.commands;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.common.Async;
import java.util.Optional;
import me.googas.api.Requests;
import me.googas.api.utility.Maps;
import me.googas.bungee.lang.BungeeLocaleFile;
import me.googas.net.api.exception.MessengerListenFailException;
import me.googas.net.sockets.json.client.JsonClient;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/** Commands for linking minecraft accounts */
public class LinkCommand {

  @Async
  @Command(aliases = "link")
  public void link(ProxiedPlayer player, BungeeLocaleFile locale, JsonClient client) {
    try {
      boolean linked =
          Requests.MinecraftLinks.isLinked(player.getUniqueId()).send(client).orElseThrow();
      if (linked) {
        player.sendMessage(locale.getComponent("link.linked"));
        return;
      }
      Optional<String> optional =
          Requests.MinecraftLinks.linkNew(player.getUniqueId()).send(client);
      if (optional.isEmpty()) {
        // TODO localize
        player.sendMessage("Failed to generate code");
        return;
      }
      String code = optional.get();
      if (code.isBlank()) {
        player.sendMessage("Failed to generate code, code is blank");
        return;
      }
      player.sendMessage(locale.getComponent("link.code", Maps.singleton("code", code)));
    } catch (MessengerListenFailException e) {
      e.printStackTrace();
      player.sendMessage("Failed to generate code, e");
    }
  }
}
