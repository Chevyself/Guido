package me.googas.bungee.commands;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.common.Async;
import me.googas.api.Requests;
import me.googas.api.links.LinkableInfo;
import me.googas.api.utility.Maps;
import me.googas.bungee.data.ProxiedOfflinePlayer;
import me.googas.bungee.lang.BungeeLocaleFile;
import me.googas.net.sockets.json.client.JsonClient;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/** Commands for linking minecraft accounts */
public class LinkCommand {

  @Async
  @Command(aliases = "link")
  public void link(ProxiedPlayer player, BungeeLocaleFile locale, JsonClient client) {
    // TODO make proxied offline player as an extra argument
    ProxiedOfflinePlayer offline = new ProxiedOfflinePlayer(player.getUniqueId(), player.getName());
    LinkableInfo link = offline.getLink();
    Requests.Links.isLinked(link)
        .send(
            client,
            Requests.ifPresentElse(
                linked -> {
                  if (!linked) {
                    Requests.Server.minecraftLinkCode(link)
                        .send(
                            client,
                            Requests.ifPresentElse(
                                code ->
                                    player.sendMessage(
                                        locale.getComponent(
                                            "link.code", Maps.singleton("code", code))),
                                () -> {
                                  // TODO could not retrieve code
                                }));
                  } else {
                    player.sendMessage(locale.getComponent("link.linked"));
                  }
                },
                () -> {
                  // TODO could not check if is linked
                }));
  }
}
