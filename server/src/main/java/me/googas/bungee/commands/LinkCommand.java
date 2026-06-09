package me.googas.bungee.commands;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.common.Async;
import java.util.concurrent.ExecutionException;
import me.googas.api.Requests;
import me.googas.api.utility.Maps;
import me.googas.bungee.lang.BungeeLocaleFile;
import me.googas.net.sockets.json.client.JsonClient;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/** Commands for linking minecraft accounts */
public class LinkCommand {

  @Async
  @Command(aliases = "link")
  public void link(ProxiedPlayer player, BungeeLocaleFile locale, JsonClient client) {
    try {
      boolean linked = Requests.MinecraftLinks.isLinked(player.getUniqueId()).future(client).get();
      if (linked) {
        player.sendMessage(locale.getComponent("link.linked"));
        return;
      }
      String code = Requests.MinecraftLinks.linkNew(player.getUniqueId()).future(client).get();
      if (code == null || code.isBlank()) {
        // TODO localize
        player.sendMessage("Failed to generate code");
        return;
      }
      player.sendMessage(locale.getComponent("link.code", Maps.singleton("code", code)));
    } catch (ExecutionException | InterruptedException e) {
      e.printStackTrace();
      player.sendMessage("Failed to generate code, e");
    }
  }
}
